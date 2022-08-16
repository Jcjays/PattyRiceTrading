package com.patriciajavier.pattyricetrading.home.admin.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.launch

class InventoryScreenViewModel : ViewModel() {

    private val _getListOfProducts = MutableLiveData<Response<List<Product>>>()
    val getListOfProducts : LiveData<Response<List<Product>>>
        get() = _getListOfProducts


    fun getListOfProducts() = viewModelScope.launch {
        FirebaseService.getListOfProductFromFireStore().collect{
            _getListOfProducts.postValue(it)
        }
    }

}