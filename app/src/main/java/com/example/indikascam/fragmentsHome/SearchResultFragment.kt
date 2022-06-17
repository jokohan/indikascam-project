package com.example.indikascam.fragmentsHome

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.R
import com.example.indikascam.adapter.ReportHistoryAdapter
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostFileRequest
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.requests.PostUserBlockRequest
import com.example.indikascam.databinding.FragmentSearchResultBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.dialog.DialogReview
import com.example.indikascam.dialog.SnackBarWarningError
import com.example.indikascam.modelsRcv.ReportHistory
import com.example.indikascam.sessionManager.SessionManager
import com.example.indikascam.util.Util
import com.example.indikascam.viewModel.SharedViewModelUser
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException


class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private val args: SearchResultFragmentArgs by navArgs()

    private val reportHistoryList = ArrayList<ReportHistory>()
    private var reportHistoryAdapter: ReportHistoryAdapter? = null

    private lateinit var sessionManager: SessionManager
    private val sharedViewModelUser: SharedViewModelUser by activityViewModels()
    private var myPhoneNumber: String? = null
    private var myBankId: Int? = null
    private var myAccountNumber: String? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        val view = binding.root

        sessionManager = SessionManager(requireContext())

        binding.searchResultFragmentTvCaptionNoReport.visibility = View.GONE
        binding.searchResultFragmentTilBankName.visibility = View.GONE

        val numberType = args.searchNumber[0]
        val number = args.searchNumber[1]

        if (number.isEmpty()) {
            binding.searchResultFragmentTvNumber.text = resources.getString(R.string.nomor_tidak_ditemukan)
        } else {
            binding.searchResultFragmentTvNumber.text = number
            val loadingDialog = DialogProgressBar.progressDialog(requireContext())
            loadingDialog.show()

            lifecycleScope.launchWhenCreated {
                val snackBar = SnackBarWarningError()
                if (numberType == "phoneNumber") {
                    sharedViewModelUser.phoneNumber.observe(viewLifecycleOwner){
                        myPhoneNumber = it
                    }
                    val response = try {
                        RetroInstance.apiHome.getSearchPhoneNumber(
                            PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"),
                            number
                        )
                    } catch (e: IOException) {
                        Log.e("loginErrorIO", e.message!!)
                        loadingDialog.dismiss()
                        return@launchWhenCreated
                    } catch (e: HttpException) {
                        Log.e("loginErrorHttp", e.message!!)
                        loadingDialog.dismiss()
                        return@launchWhenCreated
                    }
                    if (response.isSuccessful && response.body() != null) {
                        binding.searchResultFragmentRcvHistory.adapter = reportHistoryAdapter
                        binding.searchResultFragmentRcvHistory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        setupBlockPhoneNumber(number, response.body()!!.data.is_blocked)
                        val reportHistoryData = response.body()!!.data.user_reports

                        for (data in reportHistoryData) {
                            val profilePictureName = data.profile_picture.toString()
                            if(data.profile_picture != null){
                                val responseOfProfilePicture = try{
                                    RetroInstance.apiProfile.postFile(
                                        PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"),
                                        PostFileRequest("profile_pictures", profilePictureName)
                                    )
                                } catch (e: IOException) {
                                    Log.e("loginErrorIO", e.message!!)
                                    loadingDialog.dismiss()
                                    return@launchWhenCreated
                                } catch (e: HttpException) {
                                    Log.e("loginErrorHttp", e.message!!)
                                    loadingDialog.dismiss()
                                    return@launchWhenCreated
                                }
                                if(responseOfProfilePicture.isSuccessful && responseOfProfilePicture.body() != null){
                                    val bitmap = BitmapFactory.decodeStream(responseOfProfilePicture.body()?.byteStream())
                                    reportHistoryList.add(
                                        ReportHistory(
                                            data.id,
                                            bitmap,
                                            data.reporter_name,
                                            data.report_type,
                                            Util().stringToDate(data.created_at),
                                            data.status,
                                            null,
                                            null
                                        )
                                    )
                                } else{
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
                            } else{
                                reportHistoryList.add(
                                    ReportHistory(
                                        data.id,
                                        null,
                                        data.reporter_name,
                                        data.report_type,
                                        Util().stringToDate(data.created_at),
                                        data.status,
                                        null,
                                        null
                                    )
                                )
                            }
                        }
                        reportHistoryAdapter = ReportHistoryAdapter(reportHistoryList){
                            val position = it
                            val reviewDialog = DialogReview(reportHistoryList[position], args.searchNumber[0], args.searchNumber[1], myPhoneNumber, null, null)
                            reviewDialog.show(parentFragmentManager, "Dialog Review")
                        }
                        binding.searchResultFragmentRcvHistory.adapter = reportHistoryAdapter
                        binding.searchResultFragmentAcBankName.setSelection(0)
                    } else {
                        try {
                            @Suppress("BlockingMethodInNonBlockingContext") val jObjError =
                                JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.getJSONObject("error").getString("message")
                            Log.e("userBlockError", errorMessage)
                            Log.e("userBlockError", response.code().toString())
                            snackBar.showSnackBar(errorMessage, requireActivity())

                        } catch (e: Exception) {
                            Log.e("userBlockError", e.stackTraceToString())
                        }
                    }
                } else if (numberType == "accountNumber") {
                    sharedViewModelUser.bankId.observe(viewLifecycleOwner){
                        myBankId = it
                    }
                    sharedViewModelUser.bankAccountNumber.observe(viewLifecycleOwner){
                        myAccountNumber = it
                    }
                    val response = try {
                        RetroInstance.apiHome.getSearchAccountNumber(
                            PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"),
                            number
                        )
                    } catch (e: IOException) {
                        Log.e("loginErrorIO", e.message!!)
                        loadingDialog.dismiss()
                        return@launchWhenCreated
                    } catch (e: HttpException) {
                        Log.e("loginErrorHttp", e.message!!)
                        loadingDialog.dismiss()
                        return@launchWhenCreated
                    }
                    if (response.isSuccessful && response.body() != null) {
                        binding.searchResultFragmentRcvHistory.adapter = reportHistoryAdapter
                        binding.searchResultFragmentRcvHistory.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        val reportHistoryData = response.body()!!.data
                        for (data in reportHistoryData) {
                            val profilePictureName = data.profile_picture.toString()
                            if(data.profile_picture != null){
                                val responseOfProfilePicture = try{
                                    RetroInstance.apiProfile.postFile(
                                        PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"),
                                        PostFileRequest("profile_pictures", profilePictureName)
                                    )
                                } catch (e: IOException) {
                                    Log.e("loginErrorIO", e.message!!)
                                    loadingDialog.dismiss()
                                    return@launchWhenCreated
                                } catch (e: HttpException) {
                                    Log.e("loginErrorHttp", e.message!!)
                                    loadingDialog.dismiss()
                                    return@launchWhenCreated
                                }
                                if(responseOfProfilePicture.isSuccessful && responseOfProfilePicture.body() != null){
                                    val bitmap = BitmapFactory.decodeStream(responseOfProfilePicture.body()?.byteStream())
                                    reportHistoryList.add(
                                        ReportHistory(
                                            data.id,
                                            bitmap,
                                            data.reporter_name,
                                            data.report_type,
                                            Util().stringToDate(data.created_at),
                                            data.status,
                                            data.bank_id,
                                            data.bank_name
                                        )
                                    )
                                } else{
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
                            } else{
                                reportHistoryList.add(
                                    ReportHistory(
                                        data.id,
                                        null,
                                        data.reporter_name,
                                        data.report_type,
                                        Util().stringToDate(data.created_at),
                                        data.status,
                                        data.bank_id,
                                        data.bank_name
                                    )
                                )
                            }
                        }
                    } else {
                        try {
                            @Suppress("BlockingMethodInNonBlockingContext") val jObjError =
                                JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.getJSONObject("error").getString("message")
                            Log.e("userBlockError", errorMessage)
                            Log.e("userBlockError", response.code().toString())
                            snackBar.showSnackBar(errorMessage, requireActivity())

                        } catch (e: Exception) {
                            Log.e("userBlockError", e.stackTraceToString())
                        }
                    }
                    if(reportHistoryList.size > 0){
                        var bankArray = emptyArray<String>()
                        for(report in reportHistoryList){
                            bankArray += report.bankName!!
                        }
                        if(bankArray.isNotEmpty()){
                           binding.searchResultFragmentTilBankName.visibility = View.VISIBLE
                        }
                        val bankNameAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            bankArray.distinct()
                        )
                        binding.searchResultFragmentAcBankName.setAdapter(bankNameAdapter)
                        binding.searchResultFragmentAcBankName.setOnItemClickListener { _, _, i, _ ->
                            val tmp = ArrayList<ReportHistory>()
                            for(report in reportHistoryList){
                                if(report.bankName == bankArray[i]){
                                    tmp += report
                                }
                            }
                            binding.searchResultFragmentRcvHistory.adapter = null
                            reportHistoryAdapter = null
                            reportHistoryAdapter = ReportHistoryAdapter(tmp){
                                val position = it
                                val reviewDialog = DialogReview(tmp[position], args.searchNumber[0], args.searchNumber[1], myPhoneNumber, myAccountNumber, myBankId)
                                Log.i("asd", tmp[position].toString())
                                Log.i("asd", myAccountNumber.toString())
                                Log.i("asd", myBankId.toString())
                                Log.i("asd", args.toString())
                                reviewDialog.show(parentFragmentManager, "Dialog Review")
                            }
                            binding.searchResultFragmentRcvHistory.adapter = reportHistoryAdapter
                        }

                        binding.searchResultFragmentAcBankName.setText(bankArray[0],false)
                        val tmp = ArrayList<ReportHistory>()
                        tmp += reportHistoryList[0]
                        reportHistoryAdapter = ReportHistoryAdapter(tmp){
                            val position = it
                            val reviewDialog = DialogReview(tmp[position], args.searchNumber[0], args.searchNumber[1], myPhoneNumber, myAccountNumber, myBankId)

                            reviewDialog.show(parentFragmentManager, "Dialog Review")
                        }
                        binding.searchResultFragmentRcvHistory.adapter = reportHistoryAdapter
                    }
                }
                loadingDialog.dismiss()
                if(reportHistoryList.size < 1){
                    binding.searchResultFragmentTvCaptionNoReport.visibility = View.VISIBLE
                    binding.searchResultFragmentRcvHistory.visibility = View.GONE
                }else{
                    binding.searchResultFragmentTvCaptionNoReport.visibility = View.GONE
                    binding.searchResultFragmentRcvHistory.visibility = View.VISIBLE
                }
            }
        }

        if (numberType == "phoneNumber") {
            binding.searchResultFragmentTvNumberCaption.text =
                resources.getString(R.string.nomor_telepon)
        } else if (numberType == "accountNumber") {
            binding.searchResultFragmentTvNumberCaption.text =
                resources.getString(R.string.nomor_rekening)
            binding.searchResultFragmentBtnBlock.visibility = View.GONE
        }

        return view
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    private fun setupBlockPhoneNumber(number: String, isBlocked: Boolean) {
        if (isBlocked) {
            binding.searchResultFragmentBtnBlock.text = resources.getString(R.string.buka_blokir)
            binding.searchResultFragmentBtnBlock.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                R.drawable.ic_phone_enabled,
                0,
                0
            )
        } else {
            binding.searchResultFragmentBtnBlock.text = resources.getString(R.string.blokir)
            binding.searchResultFragmentBtnBlock.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                R.drawable.ic_phone_disabled,
                0,
                0
            )
        }
        binding.searchResultFragmentBtnBlock.compoundDrawableTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

        binding.searchResultFragmentBtnBlock.setOnClickListener {
            if (number.isEmpty()) {
                val toast = Toast.makeText(
                    requireContext(),
                    "Silahkan cari nomor telepon yang ingin diblokir",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            } else {
                val loadingDialog = DialogProgressBar.progressDialog(requireContext())
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())


                if (binding.searchResultFragmentBtnBlock.text == "Blokir") {
                    builder.setTitle("Blokir")
                    builder.setMessage("Anda ingin blokir panggilan masuk dari $number?")
                    builder.setIcon(R.drawable.ic_phone_disabled)
                } else if (binding.searchResultFragmentBtnBlock.text == "Buka Blokir") {
                    builder.setTitle("Buka Blokir")
                    builder.setMessage("Anda ingin buka blokir panggilan masuk dari $number?")
                    builder.setIcon(R.drawable.ic_phone_enabled)
                }

                builder.setPositiveButton("Ya") { dialogInterface, _ ->
                    loadingDialog.show()
                    val snackBar = SnackBarWarningError()
                    lifecycleScope.launchWhenCreated {
                        val response = try {
                            RetroInstance.apiHome.postUserBlock(
                                PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"),
                                PostUserBlockRequest(number)
                            )
                        } catch (e: IOException) {
                            Log.e("userBlockErrorIO", e.message!!)
                            loadingDialog.dismiss()
                            return@launchWhenCreated
                        } catch (e: HttpException) {
                            Log.e("userBlockErrorHttp", e.message!!)
                            loadingDialog.dismiss()
                            return@launchWhenCreated
                        }
                        if (response.isSuccessful && response.body() != null) {
                            snackBar.showSnackBar(response.body()!!.message, requireActivity())
                            if (binding.searchResultFragmentBtnBlock.text == "Blokir") {
                                binding.searchResultFragmentBtnBlock.text =
                                    resources.getString(R.string.buka_blokir)
                                binding.searchResultFragmentBtnBlock.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    0,
                                    R.drawable.ic_phone_enabled,
                                    0,
                                    0
                                )

                            } else if (binding.searchResultFragmentBtnBlock.text == "Buka Blokir") {
                                binding.searchResultFragmentBtnBlock.text =
                                    resources.getString(R.string.blokir)
                                binding.searchResultFragmentBtnBlock.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    0,
                                    R.drawable.ic_phone_disabled,
                                    0,
                                    0
                                )
                            }
                            binding.searchResultFragmentBtnBlock.compoundDrawableTintList =
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.white
                                    )
                                )
                        } else {
                            try {
                                @Suppress("BlockingMethodInNonBlockingContext") val jObjError =
                                    JSONObject(response.errorBody()!!.string())
                                val errorMessage =
                                    jObjError.getJSONObject("error").getString("message")
                                Log.e("userBlockError", errorMessage)
                                Log.e("userBlockError", response.code().toString())
                                snackBar.showSnackBar(errorMessage, requireActivity())

                            } catch (e: Exception) {
                                Log.e("userBlockError", e.stackTraceToString())
                            }
                        }
                        loadingDialog.dismiss()
                    }

                    dialogInterface.dismiss()
                }

                builder.setNegativeButton("Tidak") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }

                builder.show()
            }

        }
    }
}