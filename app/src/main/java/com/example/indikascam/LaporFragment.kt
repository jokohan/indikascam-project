package com.example.indikascam

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.DialogInterface
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
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.adapter.BuktiLaporanAdapter
import com.example.indikascam.databinding.FragmentLaporBinding
import com.example.indikascam.dialog.BuktiDialog
import com.example.indikascam.dialog.LaporDialog
import com.example.indikascam.model.BuktiLaporanItem
import com.example.indikascam.repository.Repository
import com.example.indikascam.sessionManager.SessionManager
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LaporFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LaporFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val args: LaporFragmentArgs by navArgs()

    private var _binding: FragmentLaporBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    private var platformFinal: Int? = null
    private var gangguanFinal: Int? = null
    private var banksFinal: Int? = null
    private var productsFinal: Int? = null
    private var buktiFinal : MutableList<MultipartBody.Part> = arrayListOf()

    private val buktiLaporanList = ArrayList<BuktiLaporanItem>()
    private val buktiLaporanListAdapter = BuktiLaporanAdapter(buktiLaporanList){
        val string = it.split(" ").toTypedArray()
        if(string[0] == "1"){
            //image Click
                if(buktiLaporanList[string[1].toInt()].type == 12){
                    val pdfIntent = Intent(Intent.ACTION_VIEW)
                    pdfIntent.setDataAndType(buktiLaporanList[string[1].toInt()].buktiImage, "application/pdf")
                    pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(pdfIntent)
                } else{
                    val buktiDialog = BuktiDialog(buktiLaporanList[string[1].toInt()].buktiImage, buktiLaporanList[string[1].toInt()].buktiTitle, buktiLaporanList[string[1].toInt()].type)
                    buktiDialog.show(parentFragmentManager, "Bukti Dialog")
                }
        } else{
            //delete click
            removeBuktiLaporanList(string[1].toInt())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLaporBinding.inflate(inflater, container, false)

        if(args.nomorYangAkanDilaporkan != null){
            val nomorYangAkanDilaporkan = args.nomorYangAkanDilaporkan
            if(nomorYangAkanDilaporkan!![1] == "0"){
                binding.laporFragmentEtNomorTeleponPelaku.setText(nomorYangAkanDilaporkan[0])
            } else if(nomorYangAkanDilaporkan[1] == "1"){
                binding.laporFragmentEtNomorRekeningPelaku.setText(nomorYangAkanDilaporkan[0])
            }
        }

        binding.laporFragmentAcGangguan.addTextChangedListener(object:
            TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!binding.laporFragmentAcGangguan.text.isNullOrEmpty()) {
                    if(binding.laporFragmentAcGangguan.text.toString() == "Penipuan"){
                        binding.laporFragmentLlPilihGangguanDahulu.visibility = View.VISIBLE
                        binding.laporFragmentLlGangguanBukanPenipuan.visibility = View.VISIBLE
                        binding.laporFragmentLlGangguanBukanPenipuan2.visibility = View.VISIBLE
                    } else{
                        binding.laporFragmentLlPilihGangguanDahulu.visibility = View.VISIBLE
                        binding.laporFragmentLlGangguanBukanPenipuan.visibility = View.GONE
                        binding.laporFragmentLlGangguanBukanPenipuan2.visibility = View.GONE
                        binding.laporFragmentCbNomorRekening.isChecked = false
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        binding.laporFragmentCbNomorRekening.setOnCheckedChangeListener { compoundButton, b ->
            if(binding.laporFragmentCbNomorRekening.isChecked){
                binding.laporFragmentClRekening.visibility = View.VISIBLE
               (binding.laporFragmentTilNamaPelaku.layoutParams as ViewGroup.MarginLayoutParams).apply {
                   setMargins(0,((requireContext().resources.displayMetrics.density) * 8).toInt(),0,0)
               }
            } else{
                binding.laporFragmentClRekening.visibility = View.GONE
                (binding.laporFragmentTilNamaPelaku.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0,0,0,0)
                }
            }
        }

        if(buktiLaporanList.size == 0){
            binding.laporFragmentRcvBuktiLaporan.visibility = View.GONE
        }


        binding.laporFragmentRcvBuktiLaporan.adapter = buktiLaporanListAdapter
        binding.laporFragmentRcvBuktiLaporan.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.laporFragmentBtnTambahBukti.setOnClickListener {
            selectBukti()
        }


        return binding.root
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                1 -> if (resultCode == RESULT_OK && data != null) {
                    val imageUri = data.data
                    val fileName: String?
                    val cursor = context?.contentResolver?.query(imageUri!!, null, null, null, null)
                    cursor?.moveToFirst()
                    fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor?.close()
                    tambahBukti(imageUri, fileName!!, 1)

                    val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(imageUri!!,"r", null) ?: return
                    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val fileOld = File(context?.cacheDir, fileName)
                    val outputStream = FileOutputStream(fileOld)
                    inputStream.copyTo(outputStream)

                    val requestBody: RequestBody = fileOld.asRequestBody("Image/*".toMediaTypeOrNull())
                    val multipartBody: MultipartBody.Part = MultipartBody.Part.createFormData("files[]", fileName, requestBody)
                    buktiFinal.add(multipartBody)
                }
                12 -> if (resultCode == RESULT_OK && data != null) {
                    val pdfUri = data.data
                    val fileName: String?
                    val cursor = context?.contentResolver?.query(pdfUri!!, null, null, null, null)
                    cursor?.moveToFirst()
                    fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor?.close()
                    tambahBukti(pdfUri, fileName!!, 12)

                    val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(pdfUri!!,"r", null) ?: return
                    val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                    val fileOld = File(context?.cacheDir, fileName)
                    val outputStream = FileOutputStream(fileOld)
                    inputStream.copyTo(outputStream)

                    val requestBody: RequestBody = fileOld.asRequestBody("Image/*".toMediaTypeOrNull())
                    val multipartBody: MultipartBody.Part = MultipartBody.Part.createFormData("files[]", fileName, requestBody)
                    buktiFinal.add(multipartBody)
                }
            }
        }

    }

    private fun removeBuktiLaporanList(it: Int) {
        buktiLaporanList.removeAt(it)
        buktiLaporanListAdapter.notifyDataSetChanged()
        if(buktiLaporanList.size == 0){
            binding.laporFragmentRcvBuktiLaporan.visibility=View.GONE
        }
    }

    private fun tambahBukti(imageSelected: Uri?, fileName: String, type: Int) {
        if(binding.laporFragmentRcvBuktiLaporan.visibility == View.GONE){
            binding.laporFragmentRcvBuktiLaporan.visibility = View.VISIBLE
        }

        val newItem = BuktiLaporanItem(imageSelected, fileName, type)

        buktiLaporanList.add(newItem)
        buktiLaporanListAdapter.notifyDataSetChanged()
        binding.laporFragmentRcvBuktiLaporan.smoothScrollToPosition(buktiLaporanList.size - 1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()

        getPlatforms()
        getProducts()
        getBanks()
        getReportType()

        binding.laporFragmentBtnLapor.setOnClickListener {
//            val sessionManager = SessionManager(requireContext())
//            val accessToken = sessionManager.fetchAuthToken()
//
//            val repository = Repository()
//            val viewModelFactory = MainViewModelFactory(repository)
//            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
//            viewModel.userReportPost("Bearer ${accessToken!!}", gangguanFinal!!,
//                banksFinal!!,
//                RequestBody.create("text/plain".toMediaTypeOrNull(), binding.laporFragmentEtNomorRekeningPelaku.text.toString()),
//                RequestBody.create("text/plain".toMediaTypeOrNull(), binding.laporFragmentEtNamaPelaku.text.toString()),
//                platformFinal!!,
//                productsFinal!!,
//                RequestBody.create("text/plain".toMediaTypeOrNull(), binding.laporFragmentEtKronologi.text.toString()),
//                buktiFinal,
//                binding.laporFragmentEtTotalKerugian.text.toString().toInt(),
//                RequestBody.create("text/plain".toMediaTypeOrNull(), binding.laporFragmentEtNomorTeleponPelaku.text.toString()))
//            viewModel.myResponseUserReport.observe(viewLifecycleOwner, Observer { response ->
//                if(response.isSuccessful && response.code() == 201){
//                    val jsonObject = Gson().toJsonTree(response.body()).asJsonObject
//                    Log.d("SUKSES LAPOR", jsonObject.toString())
//                    val dialog = LaporDialog()
//                    dialog.show(parentFragmentManager,"")
//                    Navigation.findNavController(binding.root).navigateUp()
//                } else {
//                    Log.d("ResponseError", response.code().toString())
//                    Log.d("ResponseError", response.message())
//                    val reader = response.errorBody()?.byteStream()
//                    Log.d("ResponseError", reader.toString())
//                }
//            })
            val dialog = LaporDialog()
            dialog.show(parentFragmentManager,"")
        }
    }

    private fun getProducts() {
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.productsGet()
        viewModel.myResponseProducts.observe(viewLifecycleOwner, Observer { response ->
            if(response.isSuccessful && response.code() == 200){
                val jsonObject = Gson().toJsonTree(response.body()).asJsonObject
                var products = emptyArray<String>()
                var productIds = emptyArray<Int>()
                for (data in jsonObject["data"].asJsonArray){
                    products += data.asJsonObject["name"].toString().removeSurrounding("\"")
                    productIds += data.asJsonObject["id"].asInt
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, products)
                binding.laporFragmentAcProduct.setAdapter(adapter)

                binding.laporFragmentAcProduct.setOnItemClickListener { _, _, i, _ ->
                    productsFinal = productIds[i]
                }
            }
        })
    }

    private fun getBanks() {
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.banksGet()
        viewModel.myResponseBanks.observe(viewLifecycleOwner, Observer { response ->
            if(response.isSuccessful && response.code() == 200){
                val jsonObject = Gson().toJsonTree(response.body()).asJsonObject
                var banks = emptyArray<String>()
                var bankIds = emptyArray<Int>()
                for (data in jsonObject["data"].asJsonArray){
                    banks += data.asJsonObject["name"].toString().removeSurrounding("\"")
                    bankIds += data.asJsonObject["id"].asInt
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, banks)
                binding.laporFragmentAcNamaBank.setAdapter(adapter)

                binding.laporFragmentAcNamaBank.setOnItemClickListener { _, _, i, _ ->
                    banksFinal = bankIds[i]
                }
            }
        })
    }

    private fun getPlatforms() {
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.platformsGet()
        viewModel.myResponsePlatforms.observe(viewLifecycleOwner, Observer { response ->
            if(response.isSuccessful && response.code() == 200){
                val jsonObject = Gson().toJsonTree(response.body()).asJsonObject
                var platforms = emptyArray<String>()
                var platformIds = emptyArray<Int>()
                for (data in jsonObject["data"].asJsonArray){
                    platforms += data.asJsonObject["name"].toString().removeSurrounding("\"")
                    platformIds += data.asJsonObject["id"].asInt
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, platforms)
                binding.laporFragmentAcPlatform.setAdapter(adapter)

                binding.laporFragmentAcPlatform.setOnItemClickListener { _, _, i, _ ->
                    platformFinal = platformIds[i]
                }
            }
        })
    }

    private fun getReportType() {

        //from string array
        val gangguan = resources.getStringArray(R.array.jenisGangguan)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_expandable_list_item_1, gangguan)
        binding.laporFragmentAcGangguan.setAdapter(adapter)

        //from API
//        val repository = Repository()
//        val viewModelFactory = MainViewModelFactory(repository)
//        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
//        viewModel.reportTypeGet()
//        viewModel.myResponseReportType.observe(viewLifecycleOwner, Observer { response ->
//            if(response.isSuccessful && response.code() == 200){
//                val jsonObject = Gson().toJsonTree(response.body()).asJsonObject
//                var reportTypes = emptyArray<String>()
//                var reportTypeIds = emptyArray<Int>()
//                for (data in jsonObject["data"].asJsonArray){
//                    reportTypes += data.asJsonObject["name"].toString().removeSurrounding("\"")
//                    reportTypeIds += data.asJsonObject["id"].asInt
//                }
//                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, reportTypes)
//                binding.laporFragmentAcGangguan.setAdapter(adapter)
//
//                binding.laporFragmentAcGangguan.setOnItemClickListener { _, _, i, _ ->
//                    gangguanFinal = reportTypeIds[i]
//                }
//            }
//        })
    }



    private fun selectBukti() {
        val choice = arrayOf<CharSequence>("Bukti Gambar", "Bukti PDF", "Cancel")
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        myAlertDialog.setTitle("Pilih Bukti")
        myAlertDialog.setItems(choice, DialogInterface.OnClickListener { _, item ->
            when {
                choice[item] == "Bukti Gambar" -> {
                    val pickFromGallery = Intent(Intent.ACTION_PICK)
                    pickFromGallery.type = "image/*"
                    startActivityForResult(pickFromGallery, 1)
                }
                choice[item] == "Bukti PDF" -> {
                    val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
                    pdfIntent.type = "application/pdf"
                    pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    startActivityForResult(pdfIntent, 12)
                }
                choice[item] == "Cancel" -> {

                }
            }
        })
        myAlertDialog.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LaporFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LaporFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}