package com.patriciajavier.pattyricetrading.home.admin.arch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.firestore.FirestoreService
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.firestore.models.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AccountScreenViewModel : ViewModel(){

    private val _listOfUser = MutableLiveData<DataOrException<List<User>, Exception>>()
    val listOfUser : LiveData<DataOrException<List<User>, Exception>>
        get() = _listOfUser


    private var service : Job? = null
    fun getAllUsers(){
        if(service != null) return

            // to prevent constant calling of users.
        if(_listOfUser.value != null)
            return

        _listOfUser.postValue(DataOrException(isLoading = true))


        try {
            service = viewModelScope.launch {
                val users = FirestoreService.getAllUsers()
                _listOfUser.postValue(DataOrException(data = users))
            }
        }catch (e: Exception){
            _listOfUser.postValue(DataOrException(exception = e))
        }finally {
            service = null
        }
    }

}