package com.patriciajavier.pattyricetrading.registration.arch

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.patriciajavier.pattyricetrading.firestore.FirestoreService
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.firestore.models.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegistrationLoginViewModel : ViewModel(){

    private val _checkAccessRights = MutableLiveData<Boolean>()
    val checkAccessRights : LiveData<Boolean>
        get() = _checkAccessRights

    private val _userMutableLiveData = SharedRepository.userMutableLiveData
    val userMutableLiveData: LiveData<DataOrException<FirebaseUser, Exception?>>
        get() = _userMutableLiveData

    private val _isLoading = SharedRepository.isLoading
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    //preventing concurrent calls
    private var service : Job? = null
    fun checkAccessRight(userId: String){
        if(service != null) return

        try {
        service = viewModelScope.launch {
            //optional can add loading state
            val checking = FirestoreService.checkIfAdmin(userId)
            if(checking == true) _checkAccessRights.postValue(true)
            else _checkAccessRights.postValue(false)
            }
        }catch (e : Exception){
            //optional bubble up exception
        }finally {
            service = null
        }
    }

    fun clearCheckingForAccessRights(){
        _checkAccessRights.postValue(null)
    }

    fun loginWithEmailPassword(email: String, password: String){
        SharedRepository.loginWithEmailPassword(email, password)
    }

    fun createUserWithEmailPassword(userEntity : User){
        SharedRepository.createAccountWithEmailPassword(userEntity)
    }


}


