package com.patriciajavier.pattyricetrading.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.firestore.models.User

class RegistrationLoginViewModel : ViewModel(){

    private val registrationLoginRepository = RegistrationLoginRepository()

    val createUserMutableLiveData = MutableLiveData<User>()

    private val _userMutableLiveData = registrationLoginRepository.userMutableLiveData
    val userMutableLiveData: LiveData<DataOrException<FirebaseUser, Exception?>>
        get() = _userMutableLiveData

    private val _isLoading = registrationLoginRepository.isLoading
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun loginWithEmailPassword(email: String, password: String){
        registrationLoginRepository.loginWithEmailPassword(email, password)
    }

    fun createUserWithEmailPassword(userEntity : User){
        registrationLoginRepository.createAccountWithEmailPassword(userEntity)
    }
}
