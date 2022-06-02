package com.example.indikascam.fragmentsAuthentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.indikascam.R
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostLoginRequest
import com.example.indikascam.databinding.FragmentLoginBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.dialog.SnackBarWarningError
import com.example.indikascam.sessionManager.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        setupLoginFragment()

        binding.loginFragmentBtnLogin.setOnClickListener {
            val loadingDialog = DialogProgressBar.progressDialog(requireContext())
            val snackBar = SnackBarWarningError()

            val sessionManager = SessionManager(requireContext())

            val email = binding.loginFragmentEtEmail.text.toString()
            val password = binding.loginFragmentEtPassword.text.toString()

            lifecycleScope.launchWhenCreated {
                loadingDialog.show()
                val response = try {
                    RetroInstance.apiAuth.postLogin(PostLoginRequest(email, password))
                } catch (e: IOException) {
                    Log.e("loginErrorIO", e.message!!)
                    snackBar.showSnackBar("Melampaui batas waktu, silahkan coba lagi", requireActivity())
                    loadingDialog.dismiss()
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e("loginErrorHttp", e.message!!)
                    snackBar.showSnackBar("Ada yang salah, silahkan coba lagi", requireActivity())
                    loadingDialog.dismiss()
                    return@launchWhenCreated
                }
                if (response.isSuccessful && response.body() != null) {
                    sessionManager.saveAuthToken(response.body()!!.access_token)
                    val emailVerifiedAt: String? = response.body()!!.user.email_verified_at
                    if (emailVerifiedAt == null){
                        val action = LoginFragmentDirections.actionLoginFragmentToOtpFragment("register", email)
                        Navigation.findNavController(view).navigate(action)
                    }else{
                        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                } else {
                    try{
                        @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getJSONObject("error").getString("message")
                        Log.e("loginError", errorMessage)
                        Log.e("loginError", response.code().toString())
                        snackBar.showSnackBar(errorMessage, requireActivity())

                    }catch (e: Exception){
                        Log.e("loginError", e.stackTraceToString())
                    }

                }
                loadingDialog.dismiss()
            }
        }

        return view
    }

    private fun setupLoginFragment() {
        //berhasil login kembali ke halaman profil
        binding.loginFragmentBtnLogin.setOnClickListener {
            Navigation.findNavController(binding.root).navigateUp()

            //apabila sudah register dan melakukan login namun belum verifikasi akun, maka harus ke halaman OTP terlebih dahulu
//            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_otpFragment)
        }

        //melakukan register ke halaman register
        binding.loginFragmentTvRegister.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }

        //melakukan pergantian password ke halaman ubah password
        binding.loginFragmentTvChangePassword.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_changePasswordFragment)
        }


    }

}