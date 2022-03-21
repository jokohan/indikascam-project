package com.example.indikascam

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.indikascam.databinding.FragmentForgotPasswordBinding
import com.example.indikascam.model.ForgotPasswordPost
import com.example.indikascam.model.NewPasswordPost
import com.example.indikascam.repository.Repository
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewPasswordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: MainViewModel

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
        val view = inflater.inflate(R.layout.fragment_new_password, container, false)

        view.findViewById<Button>(R.id.newPasswordFragment_btn_resetPassword).setOnClickListener {

            var emailN = ""
            val newPassword = view.findViewById<TextInputEditText>(R.id.newPasswordFragment_et_password).text.toString()
            val newPasswordConfirmation = view.findViewById<TextInputEditText>(R.id.newPasswordFragment_et_passwordConfirmation).text.toString()
            sharedViewModel.email.observe(viewLifecycleOwner, {email ->
                emailN = email
            })

            val repository = Repository()
            val viewModelFactory = MainViewModelFactory(repository)
            viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
            val myPost = NewPasswordPost(emailN, newPassword, newPasswordConfirmation)
            viewModel.newPasswordPost(myPost)
            viewModel.myResponseNewPassword.observe(viewLifecycleOwner, Observer { response ->
                if (response.isSuccessful) {
                    val jsonObject = Gson().toJsonTree(response.body()).asJsonObject
                    val action = NewPasswordFragmentDirections.actionNewPasswordFragmentToAuthCongratulationFragment("Selamat! Password Anda telah berhasil diubah. Silahkan melanjutkan ke halaman login")
                    Navigation.findNavController(view).navigate(action)

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
         * @return A new instance of fragment NewPasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewPasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}