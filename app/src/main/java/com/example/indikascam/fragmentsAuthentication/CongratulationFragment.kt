package com.example.indikascam.fragmentsAuthentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.indikascam.R
import com.example.indikascam.databinding.FragmentCongratulationBinding

class CongratulationFragment : Fragment() {

    private val args: CongratulationFragmentArgs by navArgs()

    private var _binding : FragmentCongratulationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCongratulationBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.congratulationFragmentTvCaption.text = args.caption

        binding.congratulationFragmentBtnLogin.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_congratulationFragment_to_loginFragment)
        }

        //Jika sudah di halaman ini dan user menekan tombol back maka user akan menuju ke halaman login
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Navigation.findNavController(view).navigate(R.id.action_congratulationFragment_to_loginFragment)
            }
        })
        return view
    }

}