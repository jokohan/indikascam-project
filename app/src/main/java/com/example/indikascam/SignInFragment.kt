package com.example.indikascam

import android.content.Context
import android.os.Bundle
import android.se.omapi.Session
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.indikascam.model.Post
import com.example.indikascam.repository.Repository
import com.example.indikascam.sessionManager.SessionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONObject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignInFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        view.findViewById<TextView>(R.id.signInFragment_tv_signup).setOnClickListener{
            val emailVerification = true
            if (emailVerification){
                Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_signUpFragment)
            }
            else{
                Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_otcFragment)
            }
        }

        view.findViewById<TextView>(R.id.signInFragment_tv_forgotPassword).setOnClickListener{Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_forgotPasswordFragment)}

        //api
        sessionManager = SessionManager(requireContext())
        view.findViewById<Button>(R.id.signInFragment_btn_login).setOnClickListener {
            val repository = Repository()
            val viewModelFactory = MainViewModelFactory(repository)
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            val myPost = Post(view.findViewById<TextInputEditText>(R.id.signInFragment_Et_email).text.toString(), view.findViewById<TextInputEditText>(R.id.signInFragment_Et_password).text.toString())
            viewModel.pushPost(myPost)
            viewModel.myResponseLogin.observe(viewLifecycleOwner, Observer { response ->
                if (response.isSuccessful) {
                    val jsonObject = Gson().toJsonTree(response.body()).asJsonObject
                    if(jsonObject["user"].asJsonObject["email_verified_at"] != null){
                        sessionManager.saveAuthToken(jsonObject["access_token"].toString().removeSurrounding("\""))
                    }else{
                        sessionManager.saveAuthToken("")
                    }
                    sharedViewModel.saveNama(jsonObject["user"].asJsonObject["name"].toString().removeSurrounding("\""))
                    sharedViewModel.saveEmail(jsonObject["user"].asJsonObject["email"].toString().removeSurrounding("\""))
                    sharedViewModel.saveAccessToken(jsonObject["access_token"].toString().removeSurrounding("\""))
                    if(jsonObject["user"].asJsonObject["email_verified_at"] == null){
                            var token = ""
                            sharedViewModel.accessToken.observe(viewLifecycleOwner, Observer {accessToken ->
                                token = "Bearer $accessToken"
                            })
                        Log.d("resendsignin", token)
                            viewModel.resendEmailTokenPost(token)
                            viewModel.myResponseResendEmailToken.observe(viewLifecycleOwner, Observer { response ->
                                if(response.isSuccessful){
                                    Navigation.findNavController(view).navigate(R.id.action_signInFragment_to_otcFragment)
                                } else{
                                    Log.d("ResponseError", response.code().toString())
                                    Log.d("ResponseError", response.message())
                                }
                            })

                    } else {
                        Navigation.findNavController(view).navigateUp()
                    }
                } else {
                    Log.d("responserror", response.message())
                    Log.d("responserror", response.code().toString())
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
         * @return A new instance of fragment SignInFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignInFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
