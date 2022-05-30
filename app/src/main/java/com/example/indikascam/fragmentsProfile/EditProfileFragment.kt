package com.example.indikascam.fragmentsProfile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.indikascam.R
import com.example.indikascam.databinding.FragmentEditProfileBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class EditProfileFragment : Fragment() {

    private var _binding : FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.editProfileFragmentTvEditDone.setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        binding.editProfileFragmentBtnDeleteProfilePicture.setOnClickListener {
            binding.editProfileFragmentIvProfilePicture.setImageResource(R.drawable.ic_profile)
        }

        binding.editProfileFragmentBtnPickProfilePicture.setOnClickListener {

            val pickFromGallery = Intent(Intent.ACTION_PICK)
            pickFromGallery.type = "image/*"
            startActivityForResult(pickFromGallery, 1)

        }

        return view
    }

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

                val parcelFileDescriptor = context?.contentResolver?.openFileDescriptor(imageUri!!,"r", null) ?: return
                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val fileOld = File(context?.cacheDir, fileName)
                val outputStream = FileOutputStream(fileOld)
                inputStream.copyTo(outputStream)

//                val requestBody: RequestBody = fileOld.asRequestBody("Image/*".toMediaTypeOrNull())
//                val multipartBody: MultipartBody.Part = MultipartBody.Part.createFormData("files[]", fileName, requestBody)
            }
        }
    }

}