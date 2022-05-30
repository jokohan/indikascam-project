package com.example.indikascam.fragmentsReport

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.R
import com.example.indikascam.adapter.ProofAdapter
import com.example.indikascam.databinding.FragmentReportBinding
import com.example.indikascam.dialog.DialogThankYouForReporting
import com.example.indikascam.dialog.DialogZoomInProof
import com.example.indikascam.modelsRcv.Proof
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ReportFragment : Fragment() {

    private var _binding : FragmentReportBinding? = null
    private val binding get() = _binding!!

    private val args: ReportFragmentArgs by navArgs()

    private val proofList = ArrayList<Proof>()
    private val proofAdapter = ProofAdapter(proofList, "reportFragment"){
        val string = it.split(" ").toTypedArray()
        val intention = string[0]
        val position = string[1].toInt()
        if(intention == "zoomFile"){
            if (proofList[position].isItImage){
                val zoomInProofDialog = DialogZoomInProof(proofList[position].image, proofList[position].title, proofList[position].isItImage)
                zoomInProofDialog.show(parentFragmentManager, "Zoom In Proof")
            } else{
                val pdfIntent = Intent(Intent.ACTION_VIEW)
                pdfIntent.setDataAndType(proofList[position].image, "application/pdf")
                pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(pdfIntent)
            }
        }else if(intention == "deleteFile"){
            removeProofFromProofListAndUpdateUI(position)
        }
    }

    private var finalProof : MutableList<MultipartBody.Part> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        val view = binding.root

        //nomor yang ingin di laporkan berasal dari halaman lain
        quickReportNumber()

        //munculkan form yang diperlukan setelah memilih jenis gangguan
        requiredFormAfterSelecting()

        binding.reportFragmentRcvProof.adapter = proofAdapter
        binding.reportFragmentRcvProof.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.reportFragmentBtnAddProof.setOnClickListener {
            selectProof()
        }

        if(proofList.size == 0){
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
//            finalProof
            Navigation.findNavController(binding.root).navigate(R.id.action_reportFragment_to_homeFragment)
            val dialogThankYou = DialogThankYouForReporting()
            dialogThankYou.show(parentFragmentManager, "")
        }

    }

    private fun getPlatforms() {

    }

    private fun getBanks() {

    }

    private fun getProducts() {

    }

    private fun getReportType() {
        //sementara
        val reportType = resources.getStringArray(R.array.typeReport)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_expandable_list_item_1, reportType)
        binding.reportFragmentAcTypeReport.setAdapter(adapter)
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

    private fun requiredFormAfterSelecting() {
        binding.reportFragmentAcTypeReport.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!binding.reportFragmentAcTypeReport.text.isNullOrEmpty()){
                    if(binding.reportFragmentAcTypeReport.text.toString() == "Penipu"){
                        binding.reportFragmentLlChooseTypeReportFirst.visibility = View.VISIBLE
                        binding.reportFragmentLlTypeReportExcludeScam.visibility = View.VISIBLE
                    }else{
                        binding.reportFragmentLlChooseTypeReportFirst.visibility = View.VISIBLE
                        binding.reportFragmentLlTypeReportExcludeScam.visibility = View.GONE
                        binding.reportFragmentCbReportAccountNumberToo.isChecked = false
                    }
                }
            }
        })

        binding.reportFragmentCbReportAccountNumberToo.setOnCheckedChangeListener{_, b ->
            if(b){
                binding.reportFragmentClAccountNumber.visibility = View.VISIBLE
                (binding.reportFragmentTilName.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0,((requireContext().resources.displayMetrics.density) * 8).toInt(),0,0)
                }
            } else{
                binding.reportFragmentClAccountNumber.visibility = View.GONE
                (binding.reportFragmentTilName.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0,0,0,0)
                }
            }
        }
    }

    private fun quickReportNumber() {
        if(args.numberToReport != null){
            val tmp = args.numberToReport!!
            val number = tmp[0]
            val numberType = tmp[1]
            if(numberType == "phoneNumber"){
                binding.reportFragmentEtPhoneNumber.setText(number)
            } else if(numberType == "accountNumber"){
                binding.reportFragmentEtAccountNumber.setText(number)
            }
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun removeProofFromProofListAndUpdateUI(position: Int) {
        proofList.removeAt(position)
        proofAdapter.notifyDataSetChanged()
        if(proofList.size == 0){
            binding.reportFragmentRcvProof.visibility = View.GONE
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
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
                    tambahBukti(imageUri, fileName!!, true)

                    val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(imageUri,"r", null) ?: return
                    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val fileOld = File(context?.cacheDir, fileName)
                    val outputStream = FileOutputStream(fileOld)
                    inputStream.copyTo(outputStream)

                    val requestBody: RequestBody = fileOld.asRequestBody("Image/*".toMediaTypeOrNull())
                    val multipartBody: MultipartBody.Part = MultipartBody.Part.createFormData("files[]", fileName, requestBody)
                    finalProof.add(multipartBody)
                }
                12 -> if (resultCode == Activity.RESULT_OK && data != null) {
                    val pdfUri = data.data!!
                    val fileName: String?
                    val cursor = context?.contentResolver?.query(pdfUri, null, null, null, null)
                    cursor?.moveToFirst()
                    fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor?.close()
                    tambahBukti(pdfUri, fileName!!, false)

                    val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(pdfUri,"r", null) ?: return
                    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val fileOld = File(context?.cacheDir, fileName)
                    val outputStream = FileOutputStream(fileOld)
                    inputStream.copyTo(outputStream)

                    val requestBody: RequestBody = fileOld.asRequestBody("Image/*".toMediaTypeOrNull())
                    val multipartBody: MultipartBody.Part = MultipartBody.Part.createFormData("files[]", fileName, requestBody)
                    finalProof.add(multipartBody)
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

        if(binding.reportFragmentRcvProof.visibility == View.GONE){
            binding.reportFragmentRcvProof.visibility = View.VISIBLE
        }
    }
}