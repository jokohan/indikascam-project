package com.example.indikascam.fragmentsAuthentication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostChangePasswordRequest
import com.example.indikascam.databinding.FragmentNewPasswordBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.dialog.SnackBarWarningError
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class NewPasswordFragment : Fragment() {

    private var _binding : FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!

    private val args : NewPasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.newPasswordFragmentBtnNewPassword.setOnClickListener {
            val email = args.email
            val newPassword = binding.newPasswordFragmentEtPassword.text.toString()
            val confirmPassword = binding.newPasswordFragmentEtConfirmPassword.text.toString()

            val loadingDialog = DialogProgressBar.progressDialog(requireContext())
            val snackBar = SnackBarWarningError()

            lifecycleScope.launchWhenCreated {
                loadingDialog.show()
                val response = try{
                    RetroInstance.apiAuth.postChangePassword(PostChangePasswordRequest(email, newPassword, confirmPassword))
                }catch (e: IOException) {
                    Log.e("NewPasswordErrorIO", e.message!!)
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e("NewPasswordErrorHttp", e.message!!)
                    return@launchWhenCreated
                }
                if(response.isSuccessful && response.body() != null){
                    Log.i("New Password Berhasil", response.body()!!.message)
                    val action = NewPasswordFragmentDirections.actionNewPasswordFragmentToCongratulationFragment("Selamat Anda telah berhasil mengubah kata sandi. Silahkan lanjutkan :)")
                    Navigation.findNavController(view).navigate(action)
                }else{
                    try{
                        @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getJSONObject("error").getString("message")
                        Log.e("NewPasswordError", errorMessage)
                        Log.e("NewPasswordError", response.code().toString())
                        snackBar.showSnackBar(errorMessage, requireActivity())
                    }catch (e: Exception){
                        Log.e("NewPasswordError", e.toString())
                    }
                }
                loadingDialog.dismiss()
            }

        }

        return view

    }
}