package com.example.indikascam.fragmentsProfile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.BuildConfig
import com.example.indikascam.R
import com.example.indikascam.adapter.ProofDetailAdapter
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostFileRequest
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.databinding.FragmentMyReportDetailBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.dialog.DialogZoomInProofDetail
import com.example.indikascam.modelsRcv.ProofDetail
import com.example.indikascam.sessionManager.SessionManager
import com.example.indikascam.util.Util
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.io.InputStream

class MyReportDetailFragment : Fragment() {
    private var _binding : FragmentMyReportDetailBinding? = null
    private val binding get() = _binding!!

    private val args: MyReportDetailFragmentArgs by navArgs()

    private val proofDetailList = ArrayList<ProofDetail>()
    private val proofDetailAdapter = ProofDetailAdapter(proofDetailList, "MyReportDetailFragment"){
        val string = it.split(" ").toTypedArray()
        val intention = string[0]
        val position = string[1].toInt()
        if(intention == "zoomFile"){
            if (proofDetailList[position].isItImage){
                val zoomInProofDialog = DialogZoomInProofDetail(proofDetailList[position].image as Bitmap, proofDetailList[position].title, proofDetailList[position].isItImage)
                zoomInProofDialog.show(parentFragmentManager, "Zoom In Proof")
            } else{
                openPDFContent(requireContext(), proofDetailList[position].image as InputStream, proofDetailList[position].title)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReportDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        val sessionManager = SessionManager(requireContext())
        val loadingDialog = DialogProgressBar.progressDialog(requireContext())
        lifecycleScope.launchWhenCreated {
            loadingDialog.show()
            val response = try{
                RetroInstance.apiProfile.getMyReportDetail(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"), args.reportId.toString())
            }catch (e: IOException){
                Log.e("MyReportDetailIOError", e.message!!)
                loadingDialog.dismiss()
                return@launchWhenCreated
            }catch (e: HttpException){
                Log.e("MyReportDetailHttpError", e.message!!)
                loadingDialog.dismiss()
                return@launchWhenCreated
            }
            if(response.isSuccessful && response.body() != null){
                binding.myReportDetailFragmentTvReportNumber.text = response.body()!!.data.report_number
                binding.myReportDetailFragmentTvTypeReport.text = response.body()!!.data.report_type
                binding.myReportDetailFragmentTvTime.text = response.body()!!.data.created_at
                binding.myReportDetailFragmentTvStatus.text = response.body()!!.data.status
                binding.myReportDetailFragmentTvName.text = if (response.body()!!.data.scammer_name == null) "-" else response.body()!!.data.scammer_name
                binding.myReportDetailFragmentTvPhoneNumber.text = if (response.body()!!.data.scammer_phone_number == null) "-" else response.body()!!.data.scammer_phone_number
                binding.myReportDetailFragmentTvAccountNumber.text = if (response.body()!!.data.bank_account_number == null) "-" else response.body()!!.data.bank_account_number
                binding.myReportDetailFragmentTvBankName.text = if (response.body()!!.data.bank_name == null) "-" else response.body()!!.data.bank_name
                binding.myReportDetailFragmentTvPlatform.text = if (response.body()!!.data.platform_name == null) "-" else response.body()!!.data.platform_name
                binding.myReportDetailFragmentTvProduct.text = if (response.body()!!.data.product_name == null) "-" else response.body()!!.data.product_name
                binding.myReportDetailFragmentTvTotalLoss.text = if (response.body()!!.data.total_loss == null) "-" else response.body()!!.data.total_loss.toString()
                binding.myReportDetailFragmentTvChronology.text = if (response.body()!!.data.chronology == null) "-" else response.body()!!.data.chronology

                for(file in response.body()!!.data.files){
                    val responseForFile = try{
                        RetroInstance.apiProfile.postFile(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"), PostFileRequest("user_report_files", file.server_file_name))
                    }catch (e: IOException){
                        Log.e("MyReportDetailIOError", e.message!!)
                        loadingDialog.dismiss()
                        return@launchWhenCreated
                    }catch (e: HttpException){
                        Log.e("MyReportDetailHttpError", e.message!!)
                        loadingDialog.dismiss()
                        return@launchWhenCreated
                    }
                    if(responseForFile.isSuccessful && responseForFile.body() != null){
                        val tmpFile = responseForFile.body()!!
                        val tmpFileExtension = file.server_file_name.substring(file.server_file_name.length - 4, file.server_file_name.length)
                        proofDetailList.add(ProofDetail(
                            if(tmpFileExtension != ".pdf") BitmapFactory.decodeStream(tmpFile.byteStream()) else tmpFile.byteStream(),
                            file.client_file_name,
                            tmpFileExtension != ".pdf"))
                    }else{
                        try{
                            @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.getJSONObject("error").getString("message")
                            Log.e("asdError", errorMessage)
                            Log.e("asdError", response.code().toString())
                        }catch (e: Exception){
                            Log.e("logoutError", e.toString())
                        }
                    }
                }
                proofDetailAdapter.notifyDataSetChanged()

            }else{
                try{
                    @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.getJSONObject("error").getString("message")
                    Log.e("logoutError", errorMessage)
                    Log.e("logoutError", response.code().toString())
                }catch (e: Exception){
                    Log.e("logoutError", e.toString())
                }
            }
            loadingDialog.dismiss()
        }

//        binding.myReportDetailFragmentTvReportNumber.text = args.reportId.toString()
//
//        for(i in 0..5){
//            val imageUri: Uri = Uri.Builder()
//                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
//                .authority(resources.getResourcePackageName(R.drawable.ic_flag))
//                .appendPath(resources.getResourceTypeName(R.drawable.ic_flag))
//                .appendPath(resources.getResourceEntryName(R.drawable.ic_flag))
//                .build()
//            val newItem = Proof(imageUri, proofList.size.toString(),true)
//            proofList.add(newItem)
//        }

        binding.myReportDetailFragmentRcvProof.adapter = proofDetailAdapter
        binding.myReportDetailFragmentRcvProof.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        return view
    }
    private fun openPDFContent(context: Context, inputStream: InputStream, fileName: String) {
        //saving in cache directory
        Log.i("asd", inputStream.toString())
        var file : File? = null
        if(inputStream.toString() == "[size=0].inputStream()"){
            file = File(context.externalCacheDir?.absolutePath ?: context.cacheDir.absolutePath, fileName)
        }else{
            val filePath = context.externalCacheDir?.absolutePath ?: context.cacheDir.absolutePath
            val fileNameExtension = fileName.ifEmpty { context.getString(R.string.app_name) + ".pdf" }
            file = inputStream.saveToFile(filePath, fileNameExtension)
        }
        val uri = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider", file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, "application/pdf")
        }
        context.startActivity(Intent.createChooser(intent, "Select app"))
    }

    private fun InputStream.saveToFile(filePath: String, fileName: String): File = use { input ->
        val file = File(filePath, fileName)
        file.outputStream().use { output ->
            input.copyTo(output)
        }
        input.close()
        file
    }
}