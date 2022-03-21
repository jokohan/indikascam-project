package com.example.indikascam

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    private var _nama = MutableLiveData("nama")
    private var _email = MutableLiveData("email")
    private var _accessToken = MutableLiveData("")

    val nama: LiveData<String> = _nama
    val email: LiveData<String> = _email
    val accessToken: LiveData<String> = _accessToken

    fun saveNama(newNama: String){
        _nama.value = newNama
    }

    fun saveEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun saveAccessToken(newAccessToken: String){
        _accessToken.value = newAccessToken
    }
}