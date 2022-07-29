package com.patriciajavier.pattyricetrading.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.patriciajavier.pattyricetrading.firestore.FirestoreService
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.firestore.models.User
import kotlinx.coroutines.launch

class RegistrationLoginViewModel : ViewModel(){

    private val registrationLoginRepository = RegistrationLoginRepository()

    val checkAccessRights = MutableLiveData<Boolean>()

    private val _userMutableLiveData = registrationLoginRepository.userMutableLiveData
    val userMutableLiveData: LiveData<DataOrException<FirebaseUser, Exception?>>
        get() = _userMutableLiveData

    private val _isLoading = registrationLoginRepository.isLoading
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    suspend fun checkAccessRight(userId: String){
        checkAccessRights.value = FirestoreService.checkIfAdmin(userId)
    }

    fun loginWithEmailPassword(email: String, password: String){
        registrationLoginRepository.loginWithEmailPassword(email, password)
    }

    fun createUserWithEmailPassword(userEntity : User){
        registrationLoginRepository.createAccountWithEmailPassword(userEntity)
    }


}


