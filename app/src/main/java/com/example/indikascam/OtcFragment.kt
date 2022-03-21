package com.example.indikascam

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.indikascam.model.ChangePasswordTokenPost
import com.example.indikascam.model.EmailVerificationPost
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
 * Use the [OtcFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OtcFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    val args: OtcFragmentArgs by navArgs()
    val start = 61000L
    var timer = start
    lateinit var countDownTimer: CountDownTimer

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var viewModel: MainViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_otc, container, false)

        sharedViewModel.email.observe(viewLifecycleOwner, {email ->
            view.findViewById<TextView>(R.id.otcFragment_tv_email).text = email
        })

        val inputCode1 = view.findViewById<EditText>(R.id.otcFragment_et_inputCode1)
        val inputCode2 = view.findViewById<EditText>(R.id.otcFragment_et_inputCode2)
        val inputCode3 = view.findViewById<EditText>(R.id.otcFragment_et_inputCode3)
        val inputCode4 = view.findViewById<EditText>(R.id.otcFragment_et_inputCode4)
        val inputCode5 = view.findViewById<EditText>(R.id.otcFragment_et_inputCode5)
        val inputCode6 = view.findViewById<EditText>(R.id.otcFragment_et_inputCode6)

        inputCode1.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(inputCode1.text.count() == 1){
                    inputCode2.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        inputCode2.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(inputCode2.text.count() == 1){
                    inputCode3.requestFocus()
                }else{
                    inputCode1.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        inputCode3.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(inputCode3.text.count() == 1){
                    inputCode4.requestFocus()
                } else{
                    inputCode2.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        inputCode4.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(inputCode4.text.count() == 1){
                    inputCode5.requestFocus()
                } else{
                    inputCode3.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        inputCode5.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(inputCode5.text.count() == 1){
                    inputCode6.requestFocus()
                } else{
                    inputCode4.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        inputCode6.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(inputCode6.text.count() < 1){
                    inputCode5.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        val needs = args.needs
        val repository = Repository()
        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        var email1 = ""
        var token = ""
        sharedViewModel.accessToken.observe(viewLifecycleOwner, Observer {accessToken ->
            token = "Bearer $accessToken"
        })
        sharedViewModel.email.observe(viewLifecycleOwner, Observer { email->
            email1 = email
        })

        //api
        view.findViewById<Button>(R.id.otcFragment_btn_verification).setOnClickListener {
            val otp = "${inputCode1.text}${inputCode2.text}${inputCode3.text}${inputCode4.text}${inputCode5.text}${inputCode6.text}"
            if(needs == "resetPassword"){
                val myPost = ChangePasswordTokenPost(email1,otp)
                viewModel.changePasswordTokenPost(myPost)
                viewModel.myResponseChangePasswordToken.observe(viewLifecycleOwner, Observer { response ->
                    if(response.isSuccessful){
                        Navigation.findNavController(view).navigate(R.id.action_otcFragment_to_newPasswordFragment)
                    } else {
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        Log.d("Response", jsonObj.toString())
                    }
                })
            } else {
                val myPost = EmailVerificationPost(otp)
                Log.d("token", token)
                Log.d("otp", otp)
                viewModel.emailVerificationPost(token, myPost)
                viewModel.myResponseEmailVerification.observe(viewLifecycleOwner, Observer { response ->
                    if (response.isSuccessful) {
                        val jsonObject = Gson().toJsonTree(response.body()).asJsonObject
                        Log.d("ResponseOTC", jsonObject.toString())
                        Navigation.findNavController(view).navigate(R.id.action_otcFragment_to_authCongratulationFragment)
                    } else {
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        Log.d("Response", jsonObj.toString())
                    }
                })
            }
        }

        view.findViewById<TextView>(R.id.otcFragment_tv_kirimUlangOtc).setOnClickListener {
            view.findViewById<TextView>(R.id.otcFragment_tv_kirimUlangTimer).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.otcFragment_tv_kirimUlangOtc).isEnabled = false
            startTimer(view)
            if (needs == "resetPassword"){
                viewModel.forgotPasswordPost(ForgotPasswordPost(email1))
            } else{
                viewModel.resendEmailTokenPost(token)
            }
        }

        return view
    }

    private fun startTimer(view: View) {
        countDownTimer = object : CountDownTimer(timer,1000){
            override fun onTick(millisUntilFinished: Long) {
                timer = millisUntilFinished
                setTextTimer(view)
            }

            override fun onFinish() {
                view.findViewById<TextView>(R.id.otcFragment_tv_kirimUlangOtc).isEnabled = true
                view.findViewById<TextView>(R.id.otcFragment_tv_kirimUlangTimer).visibility = View.GONE
                timer = 61000L
            }

        }.start()
    }

    fun setTextTimer(view: View) {
        var m = (timer / 1000) / 60
        var s = (timer / 1000) % 60

        var format = String.format("%02d:%02d", m, s)

        view.findViewById<TextView>(R.id.otcFragment_tv_kirimUlangTimer).text = " ($format)"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OtcFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OtcFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}