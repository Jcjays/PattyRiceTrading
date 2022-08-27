package com.patriciajavier.pattyricetrading.registration.arch

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseUser
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.firestore.models.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel(){

    private val _checkAccessRights = MutableLiveData<Boolean>()
    val checkAccessRights : LiveData<Boolean>
        get() = _checkAccessRights

    private val _userMutableLiveData = SharedRepository.userMutableLiveData
    val userMutableLiveData: LiveData<DataOrException<FirebaseUser, Exception?>>
        get() = _userMutableLiveData


    fun checkAccessRight(userId: String) = viewModelScope.launch{
        try {
            val checking = FirebaseService.checkIfAdmin(userId)
            if(checking == true)
                _checkAccessRights.postValue(true)
            else
                _checkAccessRights.postValue(false)
        }catch (e : Exception){
            //optional bubble up exception
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


