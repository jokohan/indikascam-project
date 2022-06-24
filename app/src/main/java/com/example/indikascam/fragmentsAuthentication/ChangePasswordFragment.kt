package com.example.indikascam.fragmentsAuthentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostForgetPassword
import com.example.indikascam.databinding.FragmentChangePasswordBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.dialog.SnackBarWarningError
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class ChangePasswordFragment : Fragment() {

    private var _binding : FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.changePasswordFragmentBtnChangePassword.setOnClickListener {
            val email = binding.changePasswordFragmentEtEmail.text.toString()
            val loadingDialog = DialogProgressBar.progressDialog(requireContext())
            val snackBar = SnackBarWarningError()
            lifecycleScope.launchWhenCreated {
                loadingDialog.show()
                val response = try{
                    RetroInstance.apiAuth.postForgetPassword(PostForgetPassword(email))
                }catch (e: IOException) {
                    Log.e("forgetPasswordErrorIO", e.message!!)
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e("forgetPasswordErrorHttp", e.message!!)
                    return@launchWhenCreated
                }
                if(response.isSuccessful && response.body() != null){
                    Log.i("forget password berhasil", response.body()!!.message)
                    val action = ChangePasswordFragmentDirections.actionChangePasswordFragmentToOtpFragment("resetPassword", email)
                    Navigation.findNavController(view).navigate(action)
                } else{
                    try{
                        @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getJSONObject("error").getString("message")
                        Log.e("forgetPasswordError", errorMessage)
                        Log.e("forgetPasswordError", response.code().toString())
                        snackBar.showSnackBar(errorMessage, requireActivity())
                    }catch (e: Exception){
                        Log.e("forgetPasswordError", e.toString())
                    }
                }
                loadingDialog.dismiss()
            }

        }

        return view

    }

}