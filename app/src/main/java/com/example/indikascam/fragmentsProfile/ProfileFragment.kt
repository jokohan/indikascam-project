package com.example.indikascam.fragmentsProfile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.indikascam.R
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.GetMeRequest
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.databinding.FragmentProfileBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.dialog.SnackBarWarningError
import com.example.indikascam.sessionManager.SessionManager
import com.example.indikascam.viewModel.SharedViewModelUser
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    private val sharedViewModelUser: SharedViewModelUser by activityViewModels()

        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        sessionManager = SessionManager(requireContext())

        sharedViewModelUser.name.observe(viewLifecycleOwner) { name ->
            binding.profileFragmentTvNama.text = name
        }

            sharedViewModelUser.email.observe(viewLifecycleOwner){
                binding.profileFragmentTvEmail.text = it
            }

            sharedViewModelUser.profilePicture.observe(viewLifecycleOwner){
                if(it == null){
                    binding.profileFragmentIvProfilePicture.setImageResource(R.drawable.ic_profile)
                }else{
                    binding.profileFragmentIvProfilePicture.setImageURI(Uri.parse(it))
                    Log.i("setProfilePicture", it)
                }
            }

        binding.profileFragmentBtnLogin.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_loginFragment)
        }

        binding.profileFragmentIvEditProfile.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.profileFragmentBtnLaporanSaya.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_myReportFragment)
        }

        binding.profileFragmentBtnReviewUlangSaya.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_myReviewFragment)
        }

        binding.profileFragmentBtnSettings.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        binding.profileFragmentBtnLogout.setOnClickListener {
            val loadingDialog = DialogProgressBar.progressDialog(requireContext())
            val snackBar = SnackBarWarningError()
            val token = sessionManager.fetchAuthToken()

            lifecycleScope.launchWhenCreated {
                loadingDialog.show()
                val response = try{
                    RetroInstance.apiAuth.postLogout(PostTokenRequest("Bearer $token"))
                }catch (e: IOException) {
                    Log.e("logoutErrorIO", e.message!!)
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e("logoutErrorHttp", e.message!!)
                    return@launchWhenCreated
                }
                if(response.isSuccessful && response.body() != null){
                    sessionManager.saveAuthToken("")
                    Log.i("logout berhasil", response.body()!!.message)
                    loginOrNot(sessionManager)
                } else {
                    try{
                        @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getJSONObject("error").getString("message")
                        Log.e("logoutError", errorMessage)
                        Log.e("logoutError", response.code().toString())
                        snackBar.showSnackBar(errorMessage, requireActivity())
                    }catch (e: Exception){
                        Log.e("logoutError", e.toString())
                    }
                }
                loadingDialog.dismiss()
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        loginOrNot(sessionManager)
        me()
    }

    private fun me() {
        val snackBar = SnackBarWarningError()
        lifecycleScope.launchWhenCreated {
            val response = try{
                RetroInstance.apiAuth.getMe(GetMeRequest("Bearer ${sessionManager.fetchAuthToken()}"))
            } catch (e: IOException) {
                Log.e("loginErrorIO", e.message!!)
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e("loginErrorHttp", e.message!!)
                return@launchWhenCreated
            }
            if(response.isSuccessful && response.body() != null){
                sharedViewModelUser.saveName(response.body()!!.name)
                sharedViewModelUser.saveEmail(response.body()!!.email)
                sharedViewModelUser.savePhoneNumber(response.body()?.phone_number)
                sharedViewModelUser.saveBankAccountNumber(response.body()?.bank_account_number)
                sharedViewModelUser.saveBankId(response.body()?.bank_id)
                sharedViewModelUser.saveProfilePicture(response.body()?.profile_picture)
                sharedViewModelUser.saveIsAnonymous(response.body()!!.is_anonymous)
                sharedViewModelUser.saveProtectionLevel(response.body()!!.protection_level)
                try{
                    val profilePic = response.body()?.profile_picture
//                    val profilePicBitmap = BitmapFactory.decodeFile(profilePic)
//                    binding.profileFragmentIvProfilePicture.setImageBitmap(profilePicBitmap)

                }catch (e: Exception){
                    Log.e("meProfilePicError", e.toString())
                }
            }else{
                try{
                    @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.getJSONObject("error").getString("message")
                    Log.e("meError", errorMessage)
                    Log.e("meError", response.code().toString())
                    snackBar.showSnackBar(errorMessage, requireActivity())
                }catch (e: Exception){
                    Log.e("meError", e.toString())
                }
                sessionManager.saveAuthToken("")
            }
        }
    }

    private fun loginOrNot(sessionManager: SessionManager) {
        if(sessionManager.fetchAuthToken().isNullOrEmpty()){
            Navigation.findNavController(binding.root).navigate(R.id.action_profileFragment_to_loginFragment)
        }else{
//            binding.profileFragmentBtnLogin.visibility = View.GONE
        }
    }

}