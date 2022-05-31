package com.example.indikascam.fragmentsAuthentication

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.indikascam.R
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostChangePasswordVerificationRequest
import com.example.indikascam.api.requests.PostEmailVerificationRequest
import com.example.indikascam.api.requests.PostForgetPassword
import com.example.indikascam.databinding.FragmentOtpBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.dialog.SnackBarWarningError
import com.example.indikascam.sessionManager.SessionManager
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class OtpFragment : Fragment() {

    private var _binding : FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private val args: OtpFragmentArgs by navArgs()

    private val start = 61000L
    var timer = start
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        val view = binding.root
        val loadingDialog = DialogProgressBar.progressDialog(requireContext())
        val snackBar = SnackBarWarningError()
        val sessionManager = SessionManager(requireContext())
        val accessToken = sessionManager.fetchAuthToken()

        binding.otpFragmentTvEmail.text = args.email

        binding.otpFragmentTvResendOtpCode.setOnClickListener {
            binding.otpFragmentTvResendOtpCode.isEnabled = false
            binding.otpFragmentTvDelayTime.visibility = View.VISIBLE
            startTimer()

            @Suppress("BlockingMethodInNonBlockingContext")
            if(args.needs == "register"){
                lifecycleScope.launchWhenCreated {
                    loadingDialog.show()
                    val response = try{
                        RetroInstance.apiAuth.postResendEmailToken("Bearer $accessToken")
                    }catch (e: IOException) {
                        Log.e("otpResendRegisterErrorIO", e.message!!)
                        return@launchWhenCreated
                    } catch (e: HttpException) {
                        Log.e("otpResendRegisterErrorHttp", e.message!!)
                        return@launchWhenCreated
                    }
                    if(response.isSuccessful && response.body() != null){
                        Log.i("resend otp berhasil", response.body()!!.message)
                    }else{
                        try{
                            @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.getJSONObject("error").getString("message")
                            Log.e("otpResendRegisterError", errorMessage)
                            Log.e("otpResendRegisterError", response.code().toString())
                            snackBar.showSnackBar(errorMessage, requireActivity())
                        }catch (e: Exception){
                            Log.e("otpResendRegisterError", e.toString())
                        }
                    }
                    loadingDialog.dismiss()
                }
            } else{
                lifecycleScope.launchWhenCreated {
                    loadingDialog.show()
                    val response = try{
                        RetroInstance.apiAuth.postForgetPassword(PostForgetPassword(args.email))
                    }catch (e: IOException) {
                        Log.e("otpResendForgetPasswordErrorIO", e.message!!)
                        return@launchWhenCreated
                    } catch (e: HttpException) {
                        Log.e("otpResendForgetPasswordErrorHttp", e.message!!)
                        return@launchWhenCreated
                    }
                    if(response.isSuccessful && response.body() != null){
                        Log.i("otp resend berhasil", response.body()!!.message)
                    } else{
                        try{
                            @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.getJSONObject("error").getString("message")
                            Log.e("otpResendForgetPasswordError", errorMessage)
                            Log.e("otpResendForgetPasswordError", response.code().toString())
                            snackBar.showSnackBar(errorMessage, requireActivity())
                        }catch (e: Exception){
                            Log.e("otpResendForgetPasswordError", e.toString())
                        }
                    }
                    loadingDialog.dismiss()
                }
            }
        }

        binding.otpFragmentBtnVerification.setOnClickListener {
            val email = binding.otpFragmentTvEmail.text.toString()
            val otpToken = binding.otpFragmentEtOtpCode.text.toString()
            if(args.needs == "register"){
                lifecycleScope.launchWhenCreated {
                    loadingDialog.show()

                    val response = try{
                        RetroInstance.apiAuth.postEmailVerification(
                            "Bearer $accessToken",
                            PostEmailVerificationRequest(otpToken)
                        )
                    }catch (e: IOException) {
                        Log.e("otpErrorIO", e.message!!)
                        return@launchWhenCreated
                    } catch (e: HttpException) {
                        Log.e("otpErrorHttp", e.message!!)
                        return@launchWhenCreated
                    }
                    if(response.isSuccessful && response.body() != null){
                        Log.i("otp berhasil", response.body()!!.message)
                        val action = OtpFragmentDirections.actionOtpFragmentToCongratulationFragment("Selamat Anda telah menyelesaikan pendaftaran akun. Silahkan lanjutkan :)")
                        Navigation.findNavController(view).navigate(action)
                    }else{
                        try{
                            @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.getJSONObject("error").getString("message")
                            Log.e("otpError", errorMessage)
                            Log.e("otpError", response.code().toString())
                            snackBar.showSnackBar(errorMessage, requireActivity())
                        }catch (e: Exception){
                            Log.e("otpError", e.toString())
                        }
                    }
                    loadingDialog.dismiss()
                }

            }else{
                lifecycleScope.launchWhenCreated {
                    loadingDialog.show()
                    val response = try{
                        RetroInstance.apiAuth.postChangePasswordVerification(
                            PostChangePasswordVerificationRequest(email, otpToken)
                        )
                    }catch (e: IOException) {
                        Log.e("changePasswordVerificationErrorIO", e.message!!)
                        return@launchWhenCreated
                    } catch (e: HttpException) {
                        Log.e("changePasswordVerificationErrorHttp", e.message!!)
                        return@launchWhenCreated
                    }
                    if(response.isSuccessful && response.body() != null){
                        Log.i("change password verification berhasil", response.body()!!.message)
                        val action = OtpFragmentDirections.actionOtpFragmentToNewPasswordFragment(args.email)
                        Navigation.findNavController(view).navigate(action)
                    }else{
                        try{
                            @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.getJSONObject("error").getString("message")
                            Log.e("changePasswordVerificationError", errorMessage)
                            Log.e("changePasswordVerificationError", response.code().toString())
                            snackBar.showSnackBar(errorMessage, requireActivity())
                        }catch (e: Exception){
                            Log.e("changePasswordVerificationError", e.toString())
                        }
                    }
                    loadingDialog.dismiss()
                }

            }
        }

        return view

    }
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timer,1000){
            override fun onTick(millisUntilFinished: Long) {
                timer = millisUntilFinished
                setTextTimer()
            }

            override fun onFinish() {
                binding.otpFragmentTvResendOtpCode.isEnabled = true
                binding.otpFragmentTvDelayTime.visibility = View.GONE
                timer = 61000L
            }

        }.start()
    }

    private fun setTextTimer() {
        val m = (timer / 1000) / 60
        val s = (timer / 1000) % 60

        val format = String.format("%02d:%02d", m, s)

        binding.otpFragmentTvDelayTime.text = String.format(resources.getString(R.string.dalam_kurung),format)
    }
}