package com.example.indikascam.fragmentsProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.indikascam.R
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.requests.PostUserConfiguration
import com.example.indikascam.databinding.FragmentProtectionLevelBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.dialog.SnackBarWarningError
import com.example.indikascam.sessionManager.SessionManager
import com.example.indikascam.viewModel.SharedViewModelUser
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class ProtectionLevelFragment : Fragment() {

    private var _binding : FragmentProtectionLevelBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    private val sharedViewModelUser: SharedViewModelUser by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProtectionLevelBinding.inflate(inflater, container, false)
        val view = binding.root

        sessionManager = SessionManager(requireContext())

        var userProtectionLevel = 1

        sharedViewModelUser.protectionLevel.observe(viewLifecycleOwner){
            when(it){
                1 -> binding.protectionLevelFragmentRbProteksiRendah.isChecked = true
                2 -> binding.protectionLevelFragmentRbProteksiSedang.isChecked = true
                3 -> binding.protectionLevelFragmentRbProteksiTinggi.isChecked = true
            }
        }

        binding.protectionLevelFragmentRbProteksiTinggi.setOnCheckedChangeListener { _, b ->
            if(b){
                binding.protectionLevelFragmentTvCaption.text = getString(R.string.proteksi_tinggi_caption)
                userProtectionLevel = 3
            }
        }
        binding.protectionLevelFragmentRbProteksiSedang.setOnCheckedChangeListener { _, b ->
            if(b){
                binding.protectionLevelFragmentTvCaption.text = getString(R.string.proteksi_sedang_caption)
                userProtectionLevel = 2
            }
        }
        binding.protectionLevelFragmentRbProteksiRendah.setOnCheckedChangeListener { _, b ->
            if(b){
                binding.protectionLevelFragmentTvCaption.text = getString(R.string.proteksi_rendah_caption)
                userProtectionLevel = 1

            }
        }

        binding.protectionLevelFragmentBtnApply.setOnClickListener {
            var isAnonymous: Int? = null
            sharedViewModelUser.isAnonymous.observe(viewLifecycleOwner){
                isAnonymous = it
            }
            val loadingDialog = DialogProgressBar.progressDialog(requireContext())
            val snackBar = SnackBarWarningError()
            val token = sessionManager.fetchAuthToken()
            lifecycleScope.launchWhenCreated {
                loadingDialog.show()
                val response = try {
                    RetroInstance.apiProfile.postUserConfiguration(
                        PostTokenRequest("Bearer $token"),
                        PostUserConfiguration(isAnonymous!!, userProtectionLevel)
                    )
                } catch (e: IOException) {
                    Log.e("protectionLevelErrorIO", e.message!!)
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e("protectionLevelErrorHttp", e.message!!)
                    return@launchWhenCreated
                }
                if (response.isSuccessful && response.body() != null) {
                    Log.i("konfigurasi level proteksi berhasil", response.body()!!.message)
                    sharedViewModelUser.saveProtectionLevel(userProtectionLevel)
                    Navigation.findNavController(binding.root).navigateUp()
                } else {
                    try {
                        @Suppress("BlockingMethodInNonBlockingContext") val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getJSONObject("error").getString("message")
                        Log.e("protectionLevelError", errorMessage)
                        Log.e("protectionLevelError", response.code().toString())
                        snackBar.showSnackBar(errorMessage, requireActivity())
                    } catch (e: Exception) {
                        Log.e("protectionLevelError", e.toString())
                    }
                }
                loadingDialog.dismiss()
            }
        }

        return view
    }
}