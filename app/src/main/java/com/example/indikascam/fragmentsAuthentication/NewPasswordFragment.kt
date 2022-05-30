package com.example.indikascam.fragmentsAuthentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.indikascam.databinding.FragmentNewPasswordBinding

class NewPasswordFragment : Fragment() {

    private var _binding : FragmentNewPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.newPasswordFragmentBtnNewPassword.setOnClickListener {
            val action = NewPasswordFragmentDirections.actionNewPasswordFragmentToCongratulationFragment("Selamat Anda telah berhasil mengubah kata sandi. Silahkan lanjutkan :)")
            Navigation.findNavController(view).navigate(action)
        }

        return view

    }
}