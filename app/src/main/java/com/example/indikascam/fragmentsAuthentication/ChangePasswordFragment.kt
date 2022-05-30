package com.example.indikascam.fragmentsAuthentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.indikascam.databinding.FragmentChangePasswordBinding

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
            val action = ChangePasswordFragmentDirections.actionChangePasswordFragmentToOtpFragment("resetPassword", email)
            Navigation.findNavController(view).navigate(action)
        }

        return view

    }

}