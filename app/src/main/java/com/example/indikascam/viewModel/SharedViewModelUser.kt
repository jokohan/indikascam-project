package com.example.indikascam.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModelUser: ViewModel() {

    private var _name = MutableLiveData("nama")
    private var _email = MutableLiveData("email")
    private var _phoneNumber = MutableLiveData<String?>()
    private var _bankAccountNumber = MutableLiveData<String?>()
    private var _bankId = MutableLiveData<Int?>()
    private var _profilePicture = MutableLiveData<Bitmap?>()
    private var _isAnonymous = MutableLiveData(0)
    private var _protectionLevel = MutableLiveData(2)

    var name: LiveData<String> = _name
    var email: LiveData<String> = _email
    var phoneNumber: LiveData<String?> = _phoneNumber
    var bankAccountNumber: LiveData<String?> = _bankAccountNumber
    var bankId: LiveData<Int?> = _bankId
    var profilePicture: LiveData<Bitmap?> = _profilePicture
    var isAnonymous: LiveData<Int> = _isAnonymous
    var protectionLevel: LiveData<Int> = _protectionLevel

    fun saveName(newName: String){
        _name.value = newName
    }

    fun saveEmail(newEmail: String){
        _email.value = newEmail
    }

    fun savePhoneNumber(newPhoneNumber: String?){
        _phoneNumber.value = newPhoneNumber
    }

    fun saveBankAccountNumber(newBankAccount: String?){
        _bankAccountNumber.value = newBankAccount
    }

    fun saveBankId(newBankId: Int?){
        _bankId.value = newBankId
    }

    fun saveProfilePicture(newProfilePicture: Bitmap?){
        _profilePicture.value = newProfilePicture
    }

    fun saveIsAnonymous(newIsAnonymous: Int){
        _isAnonymous.value = newIsAnonymous
    }

    fun saveProtectionLevel(newProtectionLevel: Int){
        _protectionLevel.value = newProtectionLevel
    }

}