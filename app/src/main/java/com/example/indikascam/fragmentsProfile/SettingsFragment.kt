package com.example.indikascam.fragmentsProfile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.indikascam.R
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.requests.PostUserConfiguration
import com.example.indikascam.databinding.FragmentSettingsBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.dialog.SnackBarWarningError
import com.example.indikascam.sessionManager.SessionManager
import com.example.indikascam.viewModel.SharedViewModelUser
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    private val sharedViewModelUser: SharedViewModelUser by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        sessionManager = SessionManager(requireContext())

        sharedViewModelUser.isAnonymous.observe(viewLifecycleOwner) {
            binding.settingsFragmentSwAnonymous.isChecked = it == 1
        }

        binding.settingsFragmentClTingkatProteksi.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_settingsFragment_to_protectionLevelFragment)
        }

        binding.settingsFragmentSwAnonymous.setOnClickListener {
            val anonymous =
                if (binding.settingsFragmentSwAnonymous.isChecked) {
                    1
                } else {
                    0
                }
            val loadingDialog = DialogProgressBar.progressDialog(requireContext())
            val snackBar = SnackBarWarningError()
            val token = sessionManager.fetchAuthToken()
            var protectionLevel: Int? = null
            sharedViewModelUser.protectionLevel.observe(viewLifecycleOwner) {
                protectionLevel = it
            }
            lifecycleScope.launchWhenCreated {
                loadingDialog.show()
                val response = try {
                    RetroInstance.apiProfile.postUserConfiguration(
                        PostTokenRequest("Bearer $token"),
                        PostUserConfiguration(anonymous, protectionLevel!!)
                    )
                } catch (e: IOException) {
                    Log.e("isAnonymousIO", e.message!!)
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e("isAnonymousHttp", e.message!!)
                    return@launchWhenCreated
                }
                if (response.isSuccessful && response.body() != null) {
                    Log.i("konfigurasi anonimus berhasil", response.body()!!.message)
                } else {
                    try {
                        @Suppress("BlockingMethodInNonBlockingContext") val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getJSONObject("error").getString("message")
                        Log.e("isAnonymousError", errorMessage)
                        Log.e("isAnonymousError", response.code().toString())
                        snackBar.showSnackBar(errorMessage, requireActivity())
                    } catch (e: Exception) {
                        Log.e("isAnonymousError", e.toString())
                    }
                }
                loadingDialog.dismiss()
            }
        }

        binding.settingsFragmentSwAnonymous.setOnCheckedChangeListener { _, boolean ->

        }


        return view
    }
}