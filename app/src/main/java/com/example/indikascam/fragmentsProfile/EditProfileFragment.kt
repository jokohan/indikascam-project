package com.example.indikascam.fragmentsProfile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.indikascam.R
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.databinding.FragmentProfileEditBinding
import com.example.indikascam.dialog.SnackBarWarningError
import com.example.indikascam.sessionManager.SessionManager
import com.example.indikascam.viewModel.SharedViewModelUser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

@Suppress("DEPRECATION")
class EditProfileFragment : Fragment() {

    private var _binding : FragmentProfileEditBinding? = null
    private val binding get() = _binding!!

    private var profilePic: MultipartBody.Part? = null

    private lateinit var sessionManager: SessionManager

    private val sharedViewModelUser: SharedViewModelUser by activityViewModels()

    private var banksFinal: Int? = null

    private var initialName: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        val view = binding.root

        sessionManager = SessionManager(requireContext())

        setupEditProfile(sessionManager)

        binding.editProfileFragmentTvEditDone.setOnClickListener {
            val phoneNumberChanges = binding.editProfileFragmentEtPhoneNumber.isEnabled && !binding.editProfileFragmentEtPhoneNumber.text.isNullOrEmpty()
            val bankIdChanges = binding.hasilPencarianFragmentAcPilihBank.isEnabled && !binding.hasilPencarianFragmentAcPilihBank.text.isNullOrEmpty()
            val accountNumberChanges = binding.editProfileFragmentEtAccountNumber.isEnabled && !binding.editProfileFragmentEtAccountNumber.text.isNullOrEmpty()
            if((phoneNumberChanges || bankIdChanges || accountNumberChanges) || (initialName != binding.editProfileFragmentEtName.text.toString()) || binding.editProfileFragmentIvProfilePicture.tag != "initial"){
                var warningCaption = "Anda yakin ingin mengubah profil?"
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Pemberitahuan")
                if(phoneNumberChanges || bankIdChanges || accountNumberChanges){
                    warningCaption = "Anda hanya bisa mengatur "
                    if(phoneNumberChanges) warningCaption += if(bankIdChanges && accountNumberChanges) "NOMOR TELEPON, " else "NOMOR TELEPON "
                    if(bankIdChanges) warningCaption += if(accountNumberChanges)"NAMA BANK, " else "NAMA BANK "
                    if(accountNumberChanges) warningCaption += "NOMOR REKENING "
                    warningCaption += "1 kali"
                }
                builder.setMessage(warningCaption)
                builder.setPositiveButton("Ya"){dialogInterface, _ ->
                    val snackBar = SnackBarWarningError()
                    val sessionManager = SessionManager(requireContext())
                    lifecycleScope.launchWhenCreated {
                        val response = try{
                            RetroInstance.apiProfile.postEditProfile(
                                PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"),
                                binding.editProfileFragmentEtName.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                                if(binding.editProfileFragmentIvProfilePicture.tag == "ic profile") 0 else 1,
                                if(binding.editProfileFragmentEtAccountNumber.text.toString() == "") null else binding.editProfileFragmentEtAccountNumber.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                                banksFinal,
                                if(binding.editProfileFragmentEtPhoneNumber.text.toString() == "") null else binding.editProfileFragmentEtPhoneNumber.text.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                                profilePic
                            )
                        }catch (e: IOException) {
                            Log.e("meErrorIO", e.message!!)
                            return@launchWhenCreated
                        } catch (e: HttpException) {
                            Log.e("meErrorHttp", e.message!!)
                            return@launchWhenCreated
                        }
                        if(response.isSuccessful && response.body() != null){
                            Navigation.findNavController(view).navigateUp()
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
                        }
                    }
                    dialogInterface.dismiss()
                }
                builder.setNegativeButton("Tidak"){dialogInterface,_->
                    dialogInterface.dismiss()
                }
                builder.show()
            }
        }

        binding.editProfileFragmentBtnDeleteProfilePicture.setOnClickListener {
            binding.editProfileFragmentIvProfilePicture.setImageResource(R.drawable.ic_profile)
            binding.editProfileFragmentIvProfilePicture.tag = R.drawable.ic_profile
        }

        binding.editProfileFragmentBtnPickProfilePicture.setOnClickListener {

            val pickFromGallery = Intent(Intent.ACTION_PICK)
            pickFromGallery.type = "image/*"
            startActivityForResult(pickFromGallery, 1)

        }

        return view
    }

    private fun setupEditProfile(sessionManager: SessionManager) {
        sharedViewModelUser.profilePicture.observe(viewLifecycleOwner){
            if(it == null){
                binding.editProfileFragmentIvProfilePicture.setImageResource(R.drawable.ic_profile)
            }else{
                binding.editProfileFragmentIvProfilePicture.setImageBitmap(it)
            }
            binding.editProfileFragmentIvProfilePicture.tag = "initial"
        }

        sharedViewModelUser.name.observe(viewLifecycleOwner){
            binding.editProfileFragmentEtName.setText(it)
            initialName = it
        }

        sharedViewModelUser.phoneNumber.observe(viewLifecycleOwner){
            if(it != null){
                binding.editProfileFragmentEtPhoneNumber.setText(it)
                binding.editProfileFragmentTilPhoneNumber.isEnabled = false
                binding.editProfileFragmentEtPhoneNumber.isEnabled = false
            }
        }

        sharedViewModelUser.bankId.observe(viewLifecycleOwner){
            if(it == null){
                lifecycleScope.launchWhenCreated {
                    val response = try{
                        RetroInstance.apiReport.getBank(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"))
                    }catch (e: IOException) {
                        Log.e("bankErrorIO", e.message!!)
                        return@launchWhenCreated
                    } catch (e: HttpException) {
                        Log.e("bankErrorHttp", e.message!!)
                        return@launchWhenCreated
                    }
                    if(response.isSuccessful && response.body() != null){
                        var bankNames = emptyArray<String>()
                        var bankIds = emptyArray<Int>()
                        val banksData = response.body()!!.data
                        for(data in banksData){
                            bankNames += data.name
                            bankIds += data.id
                        }
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, bankNames)
                        binding.hasilPencarianFragmentAcPilihBank.setAdapter(adapter)

                        binding.hasilPencarianFragmentAcPilihBank.setOnItemClickListener{_, _, i, _ ->
                            banksFinal = bankIds[i]
                        }
                    }else{
                        try{
                            @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.getJSONObject("error").getString("message")
                            Log.e("bankError", errorMessage)
                            Log.e("bankError", response.code().toString())
                        }catch (e: Exception){
                            Log.e("bankError", e.toString())
                        }
                    }
                }
            }else{
                binding.editProfileFragmentTilPilihBank.isEnabled = false
                binding.hasilPencarianFragmentAcPilihBank.isEnabled = false
                lifecycleScope.launchWhenCreated {
                    val response = try{
                        RetroInstance.apiReport.getBankNameById(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"), it)
                    }catch (e: IOException) {
                        Log.e("bankErrorIO", e.message!!)
                        return@launchWhenCreated
                    } catch (e: HttpException) {
                        Log.e("bankErrorHttp", e.message!!)
                        return@launchWhenCreated
                    }
                    if(response.isSuccessful && response.body() != null){
                        var bankNames = emptyArray<String>()
                        bankNames +=  response.body()!!.data.name
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, bankNames)
                        binding.hasilPencarianFragmentAcPilihBank.setAdapter(adapter)
                        binding.hasilPencarianFragmentAcPilihBank.setText(binding.hasilPencarianFragmentAcPilihBank.adapter.getItem(0).toString())
                    }else{
                        try{
                            @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                            val errorMessage = jObjError.getJSONObject("error").getString("message")
                            Log.e("bankError", errorMessage)
                            Log.e("bankError", response.code().toString())
                        }catch (e: Exception){
                            Log.e("bankError", e.toString())
                        }
                    }
                }
            }
        }

        sharedViewModelUser.bankAccountNumber.observe(viewLifecycleOwner){
            if(it != null){
                binding.editProfileFragmentEtAccountNumber.setText(it)
                binding.editProfileFragmentTilAccountNumber.isEnabled = false
                binding.editProfileFragmentEtAccountNumber.isEnabled = false
            }
        }

        sharedViewModelUser.email.observe(viewLifecycleOwner){
            binding.editProfileFragmentEtEmail.isEnabled = false
            binding.editProfileFragmentTilEmail.isEnabled = false
            binding.editProfileFragmentEtEmail.setText(it)
        }


    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 -> if (resultCode == Activity.RESULT_OK && data != null){
                val imageUri = data.data
                val fileName: String?
                val cursor = context?.contentResolver?.query(imageUri!!, null, null, null, null)
                cursor?.moveToFirst()
                fileName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                cursor?.close()
                binding.editProfileFragmentIvProfilePicture.setImageURI(imageUri)
                binding.editProfileFragmentIvProfilePicture.tag = ""

                val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(imageUri!!,"r", null) ?: return
                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val fileOld = fileName?.let { File(context?.cacheDir, it) }
                val outputStream = FileOutputStream(fileOld)
                inputStream.copyTo(outputStream)

                val requestBody: RequestBody = fileOld!!.asRequestBody("Image/*".toMediaTypeOrNull())
                val multipartBody: MultipartBody.Part = MultipartBody.Part.createFormData("profile_picture", fileName, requestBody)
                profilePic = multipartBody
            }
        }
    }

}