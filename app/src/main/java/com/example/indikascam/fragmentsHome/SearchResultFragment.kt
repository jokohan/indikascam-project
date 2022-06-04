package com.example.indikascam.fragmentsHome

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import kotlin.collections.ArrayList


class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private val args: SearchResultFragmentArgs by navArgs()

    private val reportHistoryList = ArrayList<ReportHistory>()
    private val reportHistoryAdapter = ReportHistoryAdapter(reportHistoryList)

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        val view = binding.root

        sessionManager = SessionManager(requireContext())

        binding.searchResultFragmentTilChooseBankFirstIfAccountNumberAppearInTwoDifferentBanksOrMore.visibility =
            View.GONE

        val numberType = args.searchNumber[0]
        val number = args.searchNumber[1]

        if (number.isEmpty()) {
            binding.searchResultFragmentTvNumber.text =
                resources.getString(R.string.nomor_tidak_ditemukan)
        } else {
            binding.searchResultFragmentTvNumber.text = number
            val loadingDialog = DialogProgressBar.progressDialog(requireContext())
            loadingDialog.show()

            lifecycleScope.launchWhenCreated {
                val timeout = System.currentTimeMillis()
                val snackBar = SnackBarWarningError()
                if (numberType == "phoneNumber") {
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
                                            bitmap,
                                            data.reporter_name,
                                            data.report_type,
                                            Util().stringToDate(data.created_at),
                                            data.status
                                        )
                                    )
                                    reportHistoryAdapter.notifyDataSetChanged()
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
                                        null,
                                        data.reporter_name,
                                        data.report_type,
                                        Util().stringToDate(data.created_at),
                                        data.status
                                    )
                                )
                            }
                        }
                        reportHistoryAdapter.notifyDataSetChanged()

                        Log.i("Hasil Pencarian", (System.currentTimeMillis() - timeout).toString())
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

                }
                loadingDialog.dismiss()
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





        binding.searchResultFragmentIvReview.setOnClickListener {
            val reportToReview = ArrayList<ReportHistory>()
            for (report in reportHistoryList) {
                if (report.reportStatus == "Diterima") {
                    reportToReview.add(report)
                }
            }
            val reviewDialog = DialogReview(reportToReview, numberType, number)
            reviewDialog.show(parentFragmentManager, "Dialog Review")
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