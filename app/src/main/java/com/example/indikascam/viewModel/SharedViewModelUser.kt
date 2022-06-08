package com.example.indikascam.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.indikascam.api.responses.GetBlockStatisticsResponse

class SharedViewModelUser: ViewModel() {

    private var _name = MutableLiveData("nama")
    private var _email = MutableLiveData("email")
    private var _phoneNumber = MutableLiveData<String?>()
    private var _bankAccountNumber = MutableLiveData<String?>()
    private var _bankId = MutableLiveData<Int?>()
    private var _profilePicture = MutableLiveData<Bitmap?>()
    private var _isAnonymous = MutableLiveData(0)
    private var _protectionLevel = MutableLiveData(2)
    private var _totalBlock = MutableLiveData<Int>()
    private var _totalBlockInMonth = MutableLiveData<Int>()
    private var _totalBlockInWeek = MutableLiveData<Int>()
    private var _pieChart = MutableLiveData<GetBlockStatisticsResponse.Data.PieChart>()
    private var _barChart = MutableLiveData<List<Float>>()

    var name: LiveData<String> = _name
    var email: LiveData<String> = _email
    var phoneNumber: LiveData<String?> = _phoneNumber
    var bankAccountNumber: LiveData<String?> = _bankAccountNumber
    var bankId: LiveData<Int?> = _bankId
    var profilePicture: LiveData<Bitmap?> = _profilePicture
    var isAnonymous: LiveData<Int> = _isAnonymous
    var protectionLevel: LiveData<Int> = _protectionLevel
    var totalBlock: LiveData<Int> = _totalBlock
    var totalBlockInMonth: LiveData<Int> = _totalBlockInMonth
    var totalBlockInWeek: LiveData<Int> = _totalBlockInWeek
    var pieChart: LiveData<GetBlockStatisticsResponse.Data.PieChart> = _pieChart
    var barChart: LiveData<List<Float>> = _barChart

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

    fun saveTotalBlock(newTotalBlock: Int){
        _totalBlock.value = newTotalBlock
    }

    fun saveTotalBlockInMonth(newTotalBlockInMonth: Int){
        _totalBlockInMonth.value = newTotalBlockInMonth
    }

    fun saveTotalBlockInWeek(newTotalBlockInWeek: Int){
        _totalBlockInWeek.value = newTotalBlockInWeek
    }

    fun savePieChart(newPieChart: GetBlockStatisticsResponse.Data.PieChart){
        _pieChart.value = newPieChart
    }

    fun saveBarChart(newBarChart: List<Float>){
        _barChart.value = newBarChart
    }
}