package com.example.indikascam.fragmentsReport

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.R
import com.example.indikascam.adapter.ProofAdapter
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.databinding.FragmentReportBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.dialog.DialogThankYouForReporting
import com.example.indikascam.dialog.DialogZoomInProof
import com.example.indikascam.dialog.SnackBarWarningError
import com.example.indikascam.modelsRcv.Proof
import com.example.indikascam.sessionManager.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private val args: ReportFragmentArgs by navArgs()

    val snackBar = SnackBarWarningError()


    private val proofList = ArrayList<Proof>()
    private val proofAdapter = ProofAdapter(proofList, "reportFragment") {
        val string = it.split(" ").toTypedArray()
        val intention = string[0]
        val position = string[1].toInt()
        if (intention == "zoomFile") {
            if (proofList[position].isItImage) {
                val zoomInProofDialog = DialogZoomInProof(
                    proofList[position].image,
                    proofList[position].title,
                    proofList[position].isItImage
                )
                zoomInProofDialog.show(parentFragmentManager, "Zoom In Proof")
            } else {
                val pdfIntent = Intent(Intent.ACTION_VIEW)
                pdfIntent.setDataAndType(proofList[position].image, "application/pdf")
                pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(pdfIntent)
            }
        } else if (intention == "deleteFile") {
            removeProofFromProofListAndFinalProofAndUpdateUI(position)
        }
    }

    private var reportTypeFinal : Int? = null
    private var bankFinal: Int? = null
    private var platformFinal: Int? = null
    private var productFinal: Int? = null
    private var finalProof: MutableList<MultipartBody.Part> = arrayListOf()

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        val view = binding.root

        sessionManager = SessionManager(requireContext())

        //nomor yang ingin di laporkan berasal dari halaman lain
        quickReportNumber()

        //munculkan form yang diperlukan setelah memilih jenis gangguan
        requiredFormAfterSelectingReportType()

        binding.reportFragmentRcvProof.adapter = proofAdapter
        binding.reportFragmentRcvProof.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.reportFragmentBtnAddProof.setOnClickListener {
            if(finalProof.size + 1 > 10){
                snackBar.showSnackBar("Jumlah maksimal bukti hanya 10, gunakan PDF sebagai alternatif", requireActivity())
            }else{
                selectProof()
            }
        }

        if (proofList.size == 0) {
            binding.reportFragmentRcvProof.visibility = View.GONE
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        //isi pilihan dari gangguan, produk, nama bank, platform
        getReportType()
        getProducts()
        getBanks()
        getPlatforms()

        binding.reportFragmentBtnReport.setOnClickListener {
            val loadingDialog = DialogProgressBar.progressDialog(requireContext())
            loadingDialog.show()
            lifecycleScope.launchWhenCreated {
                val response = try{
                    if(binding.reportFragmentAcTypeReport.text.toString() == "Penipuan"){
                        RetroInstance.apiReport.postReport(
                            PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"),
                            reportTypeFinal!!,
                            if(bankFinal == null) null else bankFinal,
                            if(binding.reportFragmentEtAccountNumber.text == null) null else binding.reportFragmentEtAccountNumber.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                            if(binding.reportFragmentEtName.text == null) null else binding.reportFragmentEtName.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                            platformFinal,
                            productFinal,
                            if(binding.reportFragmentEtChronology.text == null) null else binding.reportFragmentEtChronology.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                            finalProof,
                            if(binding.reportFragmentEtTotalLoss.text.isNullOrEmpty()) null else binding.reportFragmentEtTotalLoss.text.toString().toInt(),
                            if(binding.reportFragmentEtPhoneNumber.text == null) null else binding.reportFragmentEtPhoneNumber.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                        )
                    }else{
                        RetroInstance.apiReport.postReport(
                            PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"),
                            reportTypeFinal!!,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            finalProof,
                            null,
                            if(binding.reportFragmentEtPhoneNumber.text == null) null else binding.reportFragmentEtPhoneNumber.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                        )
                    }
                }catch (e: IOException) {
                    Log.e("reportErrorIO", e.message!!)
                    return@launchWhenCreated
                }catch (e: HttpException) {
                    Log.e("reportErrorHttp", e.message!!)
                    return@launchWhenCreated
                }
                if(response.isSuccessful && response.body() != null){
                    Navigation.findNavController(binding.root).navigate(R.id.action_reportFragment_to_homeFragment)
                    val dialogThankYou = DialogThankYouForReporting()
                    dialogThankYou.show(parentFragmentManager, "")
                }else{
                    try{
                        @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getJSONObject("error").getString("message")
                        Log.e("reportError", errorMessage)
                        Log.e("reportError", response.code().toString())
                        snackBar.showSnackBar(errorMessage, requireActivity())
                    }catch (e: Exception){
                        Log.e("reportError", e.toString())
                    }
                }
                loadingDialog.dismiss()
            }

        }

    }

    private fun getPlatforms() {
        lifecycleScope.launchWhenCreated {
            val response = try {
                RetroInstance.apiReport.getPlatform(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"))
            } catch (e: IOException) {
                Log.e("platformErrorIO", e.message!!)
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e("platformErrorHttp", e.message!!)
                return@launchWhenCreated
            }
            if (response.isSuccessful && response.body() != null) {
                val platformData = response.body()!!.data
                var platformName = emptyArray<String>()
                var platformId = emptyArray<Int>()
                for (data in platformData) {
                    platformName += data.name
                    platformId += data.id
                }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    platformName
                )
                binding.reportFragmentAcPlatform.setAdapter(adapter)
                binding.reportFragmentAcPlatform.setOnItemClickListener { _, _, i, _ ->
                    platformFinal = platformId[i]
                }
            } else {
                try {
                    @Suppress("BlockingMethodInNonBlockingContext") val jObjError =
                        JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.getJSONObject("error").getString("message")
                    Log.e("platformError", errorMessage)
                    Log.e("platformError", response.code().toString())
                } catch (e: Exception) {
                    Log.e("platformError", e.toString())
                }
            }
        }
    }

    private fun getBanks() {
        lifecycleScope.launchWhenCreated {
            val response = try {
                RetroInstance.apiReport.getBank(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"))
            } catch (e: IOException) {
                Log.e("bankErrorIO", e.message!!)
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e("bankErrorHttp", e.message!!)
                return@launchWhenCreated
            }
            if (response.isSuccessful && response.body() != null) {
                val banksData = response.body()!!.data
                var banksName = emptyArray<String>()
                var banksId = emptyArray<Int>()
                for (data in banksData) {
                    banksName += data.name
                    banksId += data.id
                }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    banksName
                )
                binding.reportFragmentAcNamaBank.setAdapter(adapter)
                binding.reportFragmentAcNamaBank.setOnItemClickListener { _, _, i, _ ->
                    bankFinal = banksId[i]
                }
            } else {
                try {
                    @Suppress("BlockingMethodInNonBlockingContext") val jObjError =
                        JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.getJSONObject("error").getString("message")
                    Log.e("bankError", errorMessage)
                    Log.e("bankError", response.code().toString())
                } catch (e: Exception) {
                    Log.e("bankError", e.toString())
                }
            }
        }
    }

    private fun getProducts() {
        lifecycleScope.launchWhenCreated {
            val response = try {
                RetroInstance.apiReport.getProduct(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"))
            } catch (e: IOException) {
                Log.e("productErrorIO", e.message!!)
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e("productErrorHttp", e.message!!)
                return@launchWhenCreated
            }
            if (response.isSuccessful && response.body() != null) {
                val productData = response.body()!!.data
                var productName = emptyArray<String>()
                var productId = emptyArray<Int>()
                for (data in productData) {
                    productName += data.name
                    productId += data.id
                }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    productName
                )
                binding.reportFragmentAcProduct.setAdapter(adapter)
                binding.reportFragmentAcProduct.setOnItemClickListener { _, _, i, _ ->
                    productFinal = productId[i]
                }
            } else {
                try {
                    @Suppress("BlockingMethodInNonBlockingContext") val jObjError =
                        JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.getJSONObject("error").getString("message")
                    Log.e("platformError", errorMessage)
                    Log.e("platformError", response.code().toString())
                } catch (e: Exception) {
                    Log.e("platformError", e.toString())
                }
            }
        }
    }

    private fun getReportType() {
        lifecycleScope.launchWhenCreated {
            val response = try {
                RetroInstance.apiReport.getReportType(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"))
            } catch (e: IOException) {
                Log.e("reportTypeErrorIO", e.message!!)
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e("reportTypeErrorHttp", e.message!!)
                return@launchWhenCreated
            }
            if (response.isSuccessful && response.body() != null) {
                val reportTypeData = response.body()!!.data
                var reportTypeName = emptyArray<String>()
                var reportTypeId = emptyArray<Int>()
                for (data in reportTypeData) {
                    reportTypeName += data.name
                    reportTypeId += data.id
                }
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    reportTypeName
                )
                binding.reportFragmentAcTypeReport.setAdapter(adapter)
                binding.reportFragmentAcTypeReport.setOnItemClickListener { _, _, i, _ ->
                    reportTypeFinal = reportTypeId[i]
                }
            } else {
                try {
                    @Suppress("BlockingMethodInNonBlockingContext") val jObjError =
                        JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.getJSONObject("error").getString("message")
                    Log.e("reportTypeError", errorMessage)
                    Log.e("reportTypeError", response.code().toString())
                } catch (e: Exception) {
                    Log.e("reportTypeError", e.toString())
                }
            }
        }
    }

    private fun selectProof() {
        val choice = arrayOf<CharSequence>("Bukti Gambar", "Bukti PDF", "Cancel")
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        myAlertDialog.setTitle("Pilih Bukti")
        myAlertDialog.setItems(choice) { _, item ->
            when {
                choice[item] == "Bukti Gambar" -> {
                    val pickFromGallery = Intent(Intent.ACTION_PICK)
                    pickFromGallery.type = "image/*"
                    @Suppress("DEPRECATION")
                    startActivityForResult(pickFromGallery, 1)
                }
                choice[item] == "Bukti PDF" -> {
                    val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
                    pdfIntent.type = "application/pdf"
                    pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    @Suppress("DEPRECATION")
                    startActivityForResult(pdfIntent, 12)
                }
                choice[item] == "Cancel" -> {

                }
            }
        }
        myAlertDialog.show()
    }

    private fun requiredFormAfterSelectingReportType() {
        binding.reportFragmentAcTypeReport.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!binding.reportFragmentAcTypeReport.text.isNullOrEmpty()) {
                    if (binding.reportFragmentAcTypeReport.text.toString() == "Penipuan") {
                        binding.reportFragmentLlChooseTypeReportFirst.visibility = View.VISIBLE
                        binding.reportFragmentLlTypeReportExcludeScam.visibility = View.VISIBLE
                    } else {
                        binding.reportFragmentLlChooseTypeReportFirst.visibility = View.VISIBLE
                        binding.reportFragmentLlTypeReportExcludeScam.visibility = View.GONE
                        binding.reportFragmentCbReportAccountNumberToo.isChecked = false
                        binding.reportFragmentEtAccountNumber.text = null
                        binding.reportFragmentAcNamaBank.text = null
                        binding.reportFragmentEtName.text = null
                        binding.reportFragmentAcPlatform.text = null
                        binding.reportFragmentAcProduct.text = null
                        binding.reportFragmentEtTotalLoss.text = null
                        binding.reportFragmentEtChronology.text = null
                    }
                }
            }
        })

        binding.reportFragmentCbReportAccountNumberToo.setOnCheckedChangeListener { _, b ->
            if (b) {
                binding.reportFragmentClAccountNumber.visibility = View.VISIBLE
                (binding.reportFragmentTilName.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(
                        0,
                        ((requireContext().resources.displayMetrics.density) * 8).toInt(),
                        0,
                        0
                    )
                }
            } else {
                binding.reportFragmentClAccountNumber.visibility = View.GONE
                (binding.reportFragmentTilName.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0, 0, 0, 0)
                }
            }
        }
    }

    private fun quickReportNumber() {
        if (args.numberToReport != null) {
            val tmp = args.numberToReport!!
            val number = tmp[0]
            val numberType = tmp[1]
            if (numberType == "phoneNumber") {
                binding.reportFragmentEtPhoneNumber.setText(number)
            } else if (numberType == "accountNumber") {
                binding.reportFragmentEtAccountNumber.setText(number)
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun removeProofFromProofListAndFinalProofAndUpdateUI(position: Int) {
        proofList.removeAt(position)
        finalProof.removeAt(position)
        proofAdapter.notifyDataSetChanged()
        if (proofList.size == 0) {
            binding.reportFragmentRcvProof.visibility = View.GONE
        }
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "@Suppress(\"DEPRECATION\") super.onActivityResult(requestCode, resultCode, data)",
            "androidx.fragment.app.Fragment"
        )
    )
    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            when (requestCode) {
                1 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val imageUri = data.data!!
                    val fileName: String?
                    val cursor = context?.contentResolver?.query(imageUri, null, null, null, null)
                    cursor?.moveToFirst()
                    fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor?.close()

                    val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(imageUri, "r", null) ?: return
                    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val fileOld = File(context?.cacheDir, fileName!!)
                    val outputStream = FileOutputStream(fileOld)
                    inputStream.copyTo(outputStream)

                    if(outputStream.channel.size() > 2097152){
                        snackBar.showSnackBar("Ukuran file harus dibawah 2MB", requireActivity())
                    }else{
                        tambahBukti(imageUri, fileName, true)
                        val requestBody: RequestBody = fileOld.asRequestBody("Image/*".toMediaTypeOrNull())
                        val multipartBody: MultipartBody.Part = MultipartBody.Part.createFormData("files[]", fileName, requestBody)
                        finalProof.add(multipartBody)
                    }

                }
                12 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val pdfUri = data.data!!
                    val fileName: String?
                    val cursor = context?.contentResolver?.query(pdfUri, null, null, null, null)
                    cursor?.moveToFirst()
                    fileName =
                        cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor?.close()

                    val parcelFileDescriptor =
                        context?.contentResolver?.openFileDescriptor(pdfUri, "r", null) ?: return
                    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val fileOld = File(context?.cacheDir, fileName!!)
                    val outputStream = FileOutputStream(fileOld)
                    inputStream.copyTo(outputStream)

                    if(outputStream.channel.size() > 2097152){
                        snackBar.showSnackBar("Ukuran file harus dibawah 2MB", requireActivity())
                    }else{
                        tambahBukti(pdfUri, fileName, false)
                        val requestBody: RequestBody =
                            fileOld.asRequestBody("Image/*".toMediaTypeOrNull())
                        val multipartBody: MultipartBody.Part =
                            MultipartBody.Part.createFormData("files[]", fileName, requestBody)
                        finalProof.add(multipartBody)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun tambahBukti(imageUri: Uri, fileName: String, i: Boolean) {
        val newItem = Proof(imageUri, fileName, i)
        proofList.add(newItem)
        proofAdapter.notifyDataSetChanged()
        binding.reportFragmentRcvProof.smoothScrollToPosition(proofList.size - 1)

        if (binding.reportFragmentRcvProof.visibility == View.GONE) {
            binding.reportFragmentRcvProof.visibility = View.VISIBLE
        }
    }
}