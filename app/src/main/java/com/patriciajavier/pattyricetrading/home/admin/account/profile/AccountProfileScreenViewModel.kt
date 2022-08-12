package com.patriciajavier.pattyricetrading.home.admin.account.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.firestore.models.EditableUserInfo
import com.patriciajavier.pattyricetrading.firestore.models.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AccountProfileScreenViewModel: ViewModel() {

    private val _userInfo = MutableLiveData<DataOrException<User, Exception>>()
    val userInfo : LiveData<DataOrException<User, Exception>>
        get() = _userInfo

    private var service : Job? = null

    fun getUserInfo(uId : String){
        if(service != null) return

        _userInfo.postValue(DataOrException(isLoading = true))

        try {
            service = viewModelScope.launch {
                val users = FirebaseService.getProfileData(uId)
                _userInfo.postValue(DataOrException(data = users))
            }
        }catch (e: Exception){
            _userInfo.postValue(DataOrException(exception = e))
        }finally {
            service = null
        }
    }

    fun setStatus(uId : String){
        FirebaseService.setAccountStatus(uId)
    }

    fun updateUserProfile(newUserInfo: EditableUserInfo){
        FirebaseService.setNewUserInfo(newUserInfo)
    }
}