package com.example.indikascam.dialog

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.indikascam.R
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostReviewRequestRequest
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.modelsRcv.ReportHistory
import com.example.indikascam.sessionManager.SessionManager
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class DialogReview(private val reportHistoryList: ReportHistory, private val numberType: String, private val number: String, private val myPhoneNumber: String?, private val myAccountNumber: String?, private val myBankId: Int?) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_review, container, false)
        val sessionManager = SessionManager(requireContext())
        val loadingDialog = DialogProgressBar.progressDialog(requireContext())

        rootView.findViewById<TextView>(R.id.dialogReview_tv_numberType).text = if(numberType == "accountNumber") "Nomor Rekening" else "Nomor Telepon"
        rootView.findViewById<TextView>(R.id.dialogReview_tv_number).text = number

        rootView.findViewById<TextView>(R.id.dialogReview_tv_laporan).text = String.format(resources.getString(R.string.laporan_review), reportHistoryList.username, reportHistoryList.reportType)
        rootView.findViewById<Button>(R.id.dialogReview_btn_send).setOnClickListener {
            if(numberType == "phoneNumber"){
                if(myPhoneNumber == null){
                    Toast.makeText(requireContext(), "Anda belum mengisi nomor telepon pada profil", Toast.LENGTH_LONG).show()
                }else if(myPhoneNumber != number){
                    Toast.makeText(requireContext(), "Maaf Anda tidak bisa review laporan ini", Toast.LENGTH_LONG).show()
                }else if(myPhoneNumber == number){
                    lifecycleScope.launchWhenCreated {
                        loadingDialog.show()
                        val response = try{
                            RetroInstance.apiHome.postReviewRequest(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"),
                                PostReviewRequestRequest(reportHistoryList.id)
                            )
                        }catch (e: IOException) {
                            Log.e("logoutErrorIO", e.message!!)
                            return@launchWhenCreated
                        } catch (e: HttpException) {
                            Log.e("logoutErrorHttp", e.message!!)
                            return@launchWhenCreated
                        }
                        if(response.isSuccessful && response.body() != null){
                            Toast.makeText(requireContext(), response.body()!!.message, Toast.LENGTH_LONG).show()
                        } else {
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
                        dismiss()
                    }
                }
            }else{
                if(myAccountNumber == null || myBankId == null){
                    Toast.makeText(requireContext(), "Anda belum mengisi nomor rekening dan nama bank pada profil", Toast.LENGTH_LONG).show()
                }else if(myAccountNumber != number || myBankId != reportHistoryList.bankId){
                    Toast.makeText(requireContext(), "Maaf Anda tidak bisa review laporan ini", Toast.LENGTH_LONG).show()
                }else if(myAccountNumber == number && myBankId == reportHistoryList.bankId){
                    lifecycleScope.launchWhenCreated {
                        loadingDialog.show()
                        val response = try{
                            RetroInstance.apiHome.postReviewRequest(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"),
                                PostReviewRequestRequest(reportHistoryList.id)
                            )
                        }catch (e: IOException) {
                            Log.e("logoutErrorIO", e.message!!)
                            return@launchWhenCreated
                        } catch (e: HttpException) {
                            Log.e("logoutErrorHttp", e.message!!)
                            return@launchWhenCreated
                        }
                        if(response.isSuccessful && response.body() != null){
                            Toast.makeText(requireContext(), response.body()!!.message, Toast.LENGTH_LONG).show()
                        } else {
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
                        dismiss()
                    }
                }
            }
        }

        return rootView
    }


    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        val window: Window? = dialog!!.window
        val size = Point()

        val display: Display = window?.windowManager!!.defaultDisplay
        display.getSize(size)

        val width: Int = size.x

        window.setLayout((width * 1), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
    }
}