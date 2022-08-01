package com.patriciajavier.pattyricetrading.registration.arch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException

class LoggedInViewModel: ViewModel() {

    private val _userMutableLiveData = SharedRepository.userMutableLiveData
    val userMutableLiveData: LiveData<DataOrException<FirebaseUser, Exception?>>
        get() = _userMutableLiveData

    private val _isLoggedOutMutableLiveData = SharedRepository.isLoggedOutMutableLiveData
    val isLoggedOutLiveData : LiveData<Boolean>
        get() = _isLoggedOutMutableLiveData


    fun logOut(){
        SharedRepository.logOut()
    }

}