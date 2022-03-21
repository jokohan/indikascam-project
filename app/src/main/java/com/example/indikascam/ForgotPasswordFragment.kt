package com.example.indikascam

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.indikascam.databinding.FragmentForgotPasswordBinding
import com.example.indikascam.model.ForgotPasswordPost
import com.example.indikascam.repository.Repository
import com.example.indikascam.sessionManager.SessionManager
import com.google.gson.Gson
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var sessionManager: SessionManager
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
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.forgotPasswordFragmentBtnResetPassword.setOnClickListener {
            if(binding.forgotPasswordFragmentEtEmail.text.toString() == ""){
                Toast.makeText(context, "Silahkan isi email kamu :)", Toast.LENGTH_SHORT)
            } else{
                val repository = Repository()
                val viewModelFactory = MainViewModelFactory(repository)
                viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
                val myPost = ForgotPasswordPost(binding.forgotPasswordFragmentEtEmail.text.toString())
                viewModel.forgotPasswordPost(myPost)
                viewModel.myResponseForgotPassword.observe(viewLifecycleOwner, Observer { response ->
                    if (response.isSuccessful) {
                        sharedViewModel.saveEmail(binding.forgotPasswordFragmentEtEmail.text.toString())
                        val action = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToOtcFragment("resetPassword")
                        Navigation.findNavController(view).navigate(action)
                    } else {
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        Log.d("Response", jsonObj.toString())
                    }
                })
            }
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ForgotPasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ForgotPasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}