package com.example.indikascam.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.indikascam.R
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.requests.PostUserConfiguration
import com.example.indikascam.sessionManager.SessionManager
import com.example.indikascam.viewModel.SharedViewModelUser
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class DialogSetProtectionLevelEarly: DialogFragment() {
    private val sharedViewModelUser: SharedViewModelUser by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_set_protection_level_early, container, false)

        val sessionManager = SessionManager(requireContext())
        var isAnonymous: Int? = null
        sharedViewModelUser.isAnonymous.observe(viewLifecycleOwner){
            isAnonymous = it
        }

        var userProtectionLevel: Int = 2
        sharedViewModelUser.protectionLevel.observe(viewLifecycleOwner){
            when(it){
                1 -> rootView.findViewById<RadioButton>(R.id.dialogSetProtectionLevelEarly_rb_proteksiRendah).isChecked = true
                2 -> rootView.findViewById<RadioButton>(R.id.dialogSetProtectionLevelEarly_rb_proteksiSedang).isChecked = true
                else -> rootView.findViewById<RadioButton>(R.id.dialogSetProtectionLevelEarly_rb_proteksiTinggi).isChecked = true
            }
        }

        rootView.findViewById<RadioButton>(R.id.dialogSetProtectionLevelEarly_rb_proteksiTinggi).setOnCheckedChangeListener { _, b ->
            if(b){
                rootView.findViewById<TextView>(R.id.dialogSetProtectionLevelEarly_tv_caption).text = getString(R.string.proteksi_tinggi_caption)
                userProtectionLevel = 3
            }
        }

        rootView.findViewById<RadioButton>(R.id.dialogSetProtectionLevelEarly_rb_proteksiSedang).setOnCheckedChangeListener { _, b ->
            if(b){
                rootView.findViewById<TextView>(R.id.dialogSetProtectionLevelEarly_tv_caption).text = getString(R.string.proteksi_sedang_caption)
                userProtectionLevel = 2
            }
        }

        rootView.findViewById<RadioButton>(R.id.dialogSetProtectionLevelEarly_rb_proteksiRendah).setOnCheckedChangeListener { _, b ->
            if(b){
                rootView.findViewById<TextView>(R.id.dialogSetProtectionLevelEarly_tv_caption).text = getString(R.string.proteksi_rendah_caption)
                userProtectionLevel = 1
            }
        }

        rootView.findViewById<Button>(R.id.dialogSetProtectionLevelEarly_btn_applyProtection).setOnClickListener {
            lifecycleScope.launchWhenCreated {
                val loadingDialog = DialogProgressBar.progressDialog(requireContext())
                loadingDialog.show()
                val response = try{
                    RetroInstance.apiProfile.postUserConfiguration(
                        PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"),
                        PostUserConfiguration(if(isAnonymous == null) 1 else 0, userProtectionLevel)
                    )
                }catch (e: IOException) {
                    Log.e("protectionLevelErrorIO", e.message!!)
                    return@launchWhenCreated
                } catch (e: HttpException) {
                    Log.e("protectionLevelErrorHttp", e.message!!)
                    return@launchWhenCreated
                }
                if (response.isSuccessful && response.body() != null) {
                    Log.i("konfigurasi level proteksi berhasil", response.body()!!.message)
                    sharedViewModelUser.saveProtectionLevel(userProtectionLevel)
                    loadingDialog.dismiss()
                } else {
                    try {
                        @Suppress("BlockingMethodInNonBlockingContext") val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getJSONObject("error").getString("message")
                        Log.e("protectionLevelError", errorMessage)
                        Log.e("protectionLevelError", response.code().toString())
                    } catch (e: Exception) {
                        Log.e("protectionLevelError", e.toString())
                    }
                }
                loadingDialog.dismiss()
                dismiss()
            }
        }

        return rootView
    }
}