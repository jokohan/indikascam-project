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
import com.example.indikascam.model.Post
import com.example.indikascam.model.RegisterPost
import com.example.indikascam.repository.Repository
import com.example.indikascam.sessionManager.SessionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        sessionManager = SessionManager(requireContext())
        view.findViewById<Button>(R.id.signUpFragment_btn_signUp).setOnClickListener{
            val repository = Repository()
            val viewModelFactory = MainViewModelFactory(repository)
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            val myPost = RegisterPost(
                view.findViewById<TextInputEditText>(R.id.signUpFragment_et_fullname).text.toString(),
                view.findViewById<TextInputEditText>(R.id.signUpFragment_Et_email).text.toString(),
                view.findViewById<TextInputEditText>(R.id.signUpFragment_Et_password).text.toString(),
                view.findViewById<TextInputEditText>(R.id.signUpFragment_Et_confirmPassword).text.toString(),
            )
            Log.d("registerPost",myPost.toString())
            viewModel.registerPost(myPost)
            viewModel.myResponseRegister.observe(viewLifecycleOwner, Observer { response ->
                if (response.isSuccessful) {
                    val jsonObject = Gson().toJsonTree(response.body()).asJsonObject
                    Log.d("Register",jsonObject["message"].toString())
                    val myPostForLogin = Post(
                        view.findViewById<TextInputEditText>(R.id.signUpFragment_Et_email).text.toString(),
                        view.findViewById<TextInputEditText>(R.id.signUpFragment_Et_password).text.toString()
                    )
                    viewModel.pushPost(myPostForLogin)
                    viewModel.myResponseLogin.observe(viewLifecycleOwner, Observer { response ->
                        if(response.isSuccessful){
                            val jsonObject = Gson().toJsonTree(response.body()).asJsonObject
                            sharedViewModel.saveEmail(jsonObject["user"].asJsonObject["email"].toString().removeSurrounding("\""))
                            sharedViewModel.saveAccessToken(jsonObject["access_token"].toString().removeSurrounding("\""))
                            Log.d("atsignup", jsonObject["access_token"].toString().removeSurrounding("\""))
                            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_otcFragment)
                        } else {
                            val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                            Log.d("ResponseLoginSetelahRegister", jsonObj.toString())
                        }
                    })
                } else {
                    val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                    Log.d("Response", jsonObj.toString())
                }
            })
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
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}