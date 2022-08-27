package com.patriciajavier.pattyricetrading.registration.arch

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.firestore.models.User
import kotlinx.coroutines.launch

class LoggedInViewModel: ViewModel() {

    private val _userMutableLiveData = MutableLiveData<User>()
    val userMutableLiveData: LiveData<User>
        get() = _userMutableLiveData

    private val _isLoggedOutMutableLiveData = SharedRepository.isLoggedOutMutableLiveData
    val isLoggedOutLiveData : LiveData<Boolean>
        get() = _isLoggedOutMutableLiveData


    fun logOut(){
        SharedRepository.logOut()
    }

    fun getProfileData(uId: String) = viewModelScope.launch{
         val data =  FirebaseService.getProfileData(uId)
        _userMutableLiveData.value = data
    }

}