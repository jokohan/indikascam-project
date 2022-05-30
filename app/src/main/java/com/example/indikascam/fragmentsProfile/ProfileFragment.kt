package com.example.indikascam.fragmentsProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.indikascam.R
import com.example.indikascam.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

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
        return view
    }

}