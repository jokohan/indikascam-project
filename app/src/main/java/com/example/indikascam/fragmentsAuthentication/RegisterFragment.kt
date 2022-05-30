package com.example.indikascam.fragmentsAuthentication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostLoginRequest
import com.example.indikascam.api.requests.PostRegisterRequest
import com.example.indikascam.databinding.FragmentRegisterBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.dialog.SnackBarWarningError
import com.example.indikascam.sessionManager.SessionManager
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class RegisterFragment : Fragment() {

    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.registerFragmentBtnRegister.setOnClickListener {
            val loadingDialog = DialogProgressBar.progressDialog(requireContext())
            val snackBar = SnackBarWarningError()

            val name = binding.registerFragmentEtName.text.toString()
            val email = binding.registerFragmentEtEmail.text.toString()
            val password = binding.registerFragmentEtPassword.text.toString()
            val passwordConfirmation = binding.registerFragmentEtConfirmPassword.text.toString()
            val postRegisterRequest = PostRegisterRequest(name, email, password, passwordConfirmation)

            lifecycleScope.launchWhenCreated {
                loadingDialog.show()
                val response = try {
                    RetroInstance.apiAuth.postRegister(postRegisterRequest)
                } catch (e: IOException) {
                    Log.e("registerErrorIO", e.message!!)
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e("registerErrorHttp", e.message!!)
                    return@launchWhenCreated
                }
                if (response.isSuccessful && response.body() != null) {
                    val responseToLogin = try{
                        RetroInstance.apiAuth.postLogin(PostLoginRequest(email, password))
                    }catch (e: IOException) {
                        Log.e("loginErrorIO", e.message!!)
                        return@launchWhenCreated
                    } catch (e: HttpException) {
                        Log.e("loginErrorHttp", e.message!!)
                        return@launchWhenCreated
                    }
                    if (responseToLogin.isSuccessful && responseToLogin.body() != null) {
                        val sessionManager = SessionManager(requireContext())
                        sessionManager.saveAuthToken(responseToLogin.body()!!.access_token)
                        val action = RegisterFragmentDirections.actionRegisterFragmentToOtpFragment("register", email)
                        Navigation.findNavController(view).navigate(action)
                    } else {
                        try{
                            val jObjError = JSONObject(responseToLogin.errorBody()!!.string())
                            val errorMessage = jObjError.getJSONObject("error").getString("message")
                            Log.e("loginError", errorMessage)
                            Log.e("loginError", responseToLogin.code().toString())
                            snackBar.showSnackBar(errorMessage, requireActivity())
                        }catch (e: Exception){
                            Log.e("loginError", e.toString())
                        }
                    }
                } else {
                    try{
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getJSONObject("error").getString("message")
                        Log.e("registerError", errorMessage)
                        Log.e("registerError", response.code().toString())
                        snackBar.showSnackBar(errorMessage, requireActivity())
                    }catch (e: Exception){
                        Log.e("registerError", e.toString())
                    }
                }
                loadingDialog.dismiss()
            }


        }

        return view
    }
}