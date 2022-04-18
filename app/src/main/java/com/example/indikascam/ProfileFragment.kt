package com.example.indikascam

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.indikascam.databinding.FragmentProfileBinding
import com.example.indikascam.repository.Repository
import com.example.indikascam.sessionManager.SessionManager
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: MainViewModel

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.profileFragmentIvEditProfile.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        sessionManager = SessionManager(requireContext())

        view.findViewById<Button>(R.id.profileFragment_btn_signin).setOnClickListener{Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_signInFragment)}

        binding.profileFragmentBtnLogout.setOnClickListener {
            val repository = Repository()
            val viewModelFactory = MainViewModelFactory(repository)
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            try {
                viewModel.logoutPost("Bearer ${sessionManager.fetchAuthToken().toString()}")
                viewModel.myResponseLogout.observe(viewLifecycleOwner, Observer { response->
                    if (response.isSuccessful){
                        binding.profileFragmentTvNamaUser.text = "Nama"
                        binding.profileFragmentTvEmailUser.text = "Email"
                        binding.profileFragmentBtnLogout.isVisible = false
//                        binding.profileFragmentBtnKomentarSaya.isVisible = false
                        binding.profileFragmentBtnLaporanSaya.isVisible = false
                        binding.profileFragmentBtnSignin.isVisible = true
                        sessionManager.saveAuthToken("")
                    } else {
                        Log.d("logouterror", response.code().toString())
                        Log.d("logouterror", response.message())
                    }
                })
            } catch (e: Exception){
                Log.d("ProfileFragmentLogout", e.toString())
            }

        }

        binding.profileFragmentBtnProfilUsaha.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_profilUsahaFragment)
        }

        binding.profileFragmentBtnPengaturan.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        binding.profileFragmentBtnLaporanSaya.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_laporanSayaFragment)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.fetchAuthToken().toString() == ""){
            binding.profileFragmentTvNamaUser.text = "Nama"
            binding.profileFragmentTvEmailUser.text = "Email"
            binding.profileFragmentBtnLogout.isVisible = false
//            binding.profileFragmentBtnKomentarSaya.isVisible = false
            binding.profileFragmentBtnLaporanSaya.isVisible = false
//            binding.profileFragmentBtnSignin.isVisible = true
        } else {
            sharedViewModel.nama.observe(viewLifecycleOwner, Observer { nama->
                binding.profileFragmentTvNamaUser.text = nama
            })
            sharedViewModel.email.observe(viewLifecycleOwner, Observer { email->
                binding.profileFragmentTvEmailUser.text = email
            })
            binding.profileFragmentBtnLogout.isVisible = true
//            binding.profileFragmentBtnKomentarSaya.isVisible = true
            binding.profileFragmentBtnLaporanSaya.isVisible = true
//            binding.profileFragmentBtnSignin.isVisible = false
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
