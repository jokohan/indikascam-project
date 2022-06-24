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
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.*


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
                    snackBar.showSnackBar(e.message, requireActivity())
                    loadingDialog.dismiss()
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e("loginErrorHttp", e.message!!)
                    snackBar.showSnackBar(e.message, requireActivity())
                    loadingDialog.dismiss()
                    return@launchWhenCreated
                }
                if (response.isSuccessful && response.body() != null) {
                    sessionManager.saveAuthToken(response.body()!!.access_token)
                    sessionManager.saveExpireToken(response.body()!!.expires_in * 1000 + Date().time)
                    val emailVerifiedAt: String? = response.body()!!.user.email_verified_at
                    if (emailVerifiedAt == null) {
                        val action = LoginFragmentDirections.actionLoginFragmentToOtpFragment(
                            "register",
                            email
                        )
                        Navigation.findNavController(view).navigate(action)
                    } else {
                        Navigation.findNavController(view)
                            .navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                } else {
                    try {
                        @Suppress("BlockingMethodInNonBlockingContext")
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = if(jObjError.has("message")){
                            jObjError.getString("message")
                        }else{
                            jObjError.getJSONObject("error").getString("message")
                        }
                        Log.e("loginErrorMessage", errorMessage)
                        Log.e("loginErrorCode", response.code().toString())
                        snackBar.showSnackBar(errorMessage, requireActivity())
                    } catch (e: Exception) {
                        Log.e("loginErrorCatch", response.code().toString())
                        Log.e("loginErrorCatch", e.stackTraceToString())
                    }
                }
                loadingDialog.dismiss()
            }
        }
        return view
    }

    private fun setupLoginFragment() {
        binding.loginFragmentBtnLogin.setOnClickListener {
            Navigation.findNavController(binding.root).navigateUp()
        }

        binding.loginFragmentTvRegister.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginFragmentTvChangePassword.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_changePasswordFragment)
        }
    }
}