package com.patriciajavier.pattyricetrading.registration

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException

class LoggedInViewModel: ViewModel() {

    private val registrationLoginRepository = RegistrationLoginRepository()

    private val _userMutableLiveData = registrationLoginRepository.userMutableLiveData
    val userMutableLiveData: LiveData<DataOrException<FirebaseUser, Exception?>>
        get() = _userMutableLiveData

    private val _isLoggedOutMutableLiveData = registrationLoginRepository.isLoggedOutMutableLiveData
    val isLoggedOutLiveData : LiveData<Boolean>
        get() = _isLoggedOutMutableLiveData

    fun logOut(){
        registrationLoginRepository.logOut()
    }

}