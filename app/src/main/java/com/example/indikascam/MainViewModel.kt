package com.example.indikascam

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.indikascam.model.*
import com.example.indikascam.repository.Repository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {

    val myResponseLogin: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseRegister: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseEmailVerification: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseForgotPassword: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseChangePasswordToken: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseNewPassword: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseLogout: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseResendEmailToken: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseMe: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponsePlatforms: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseReportType: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseBanks: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseProducts: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseUserReport: MutableLiveData<Response<Any>> = MutableLiveData()
    val myResponseBlockCall: MutableLiveData<Response<Any>> = MutableLiveData()

    fun pushPost(post: Post){
        viewModelScope.launch {
            val response = repository.pushPost(post)
            myResponseLogin.value = response
        }
    }

    fun registerPost(registerPost: RegisterPost){
        viewModelScope.launch {
            val response = repository.registerPost(registerPost)
            myResponseRegister.value = response
        }
    }

    fun emailVerificationPost(token: String, emailVerificationPost: EmailVerificationPost){
        viewModelScope.launch {
            val response = repository.emailVerificationPost(token, emailVerificationPost)
            myResponseEmailVerification.value = response
        }
    }

    fun forgotPasswordPost(forgotPasswordPost: ForgotPasswordPost){
        viewModelScope.launch {
            val response = repository.forgotPasswordPost(forgotPasswordPost)
            myResponseForgotPassword.value = response
        }
    }

    fun changePasswordTokenPost(changePasswordTokenPost: ChangePasswordTokenPost){
        viewModelScope.launch {
            val response = repository.changePasswordTokenPost(changePasswordTokenPost)
            myResponseChangePasswordToken.value = response
        }
    }

    fun newPasswordPost(newPasswordPost: NewPasswordPost){
        viewModelScope.launch {
            val response = repository.newPasswordPost(newPasswordPost)
            myResponseNewPassword.value = response
        }
    }

    fun logoutPost(token: String){
        viewModelScope.launch {
            val response = repository.logoutPost(token)
            myResponseLogout.value = response
        }
    }

    fun resendEmailTokenPost(token: String){
        viewModelScope.launch {
            val response = repository.resendEmailToken(token)
            myResponseResendEmailToken.value = response
        }
    }

    fun meGet(token: String){
        viewModelScope.launch {
            val response = repository.meGet(token)
            myResponseMe.value = response
        }
    }

    fun platformsGet(){
        viewModelScope.launch {
            val response = repository.platformsGet()
            myResponsePlatforms. value = response
        }
    }

    fun productsGet(){
        viewModelScope.launch {
            val response = repository.productsGet()
            myResponseProducts. value = response
        }
    }

    fun banksGet(){
        viewModelScope.launch {
            val response = repository.banksGet()
            myResponseBanks. value = response
        }
    }

    fun reportTypeGet(){
        viewModelScope.launch {
            val response = repository.reportTypeGet()
            myResponseReportType. value = response
        }
    }

    fun userReportPost(token: String, jenisGangguan: Int, bank: Int, noRek: RequestBody, namaPenipu: RequestBody, platform: Int, product: Int, kronologi: RequestBody, bukti: List<MultipartBody.Part>, totalKerugian: Int, noTelPenipu: RequestBody){
        viewModelScope.launch {
            val response = repository.userReportPost(token, jenisGangguan, bank, noRek, namaPenipu, platform,product, kronologi, bukti, totalKerugian,noTelPenipu)
            myResponseUserReport.value = response
        }
    }


}