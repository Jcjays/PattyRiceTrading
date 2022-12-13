package com.patriciajavier.pattyricetrading.home.admin.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Logs
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import com.patriciajavier.pattyricetrading.home.admin.sales.SalesReportViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class InventoryScreenViewModel : ViewModel() {

    private val _getListOfProducts = MutableLiveData<Response<List<Product>>>()
    val getListOfProducts : LiveData<Response<List<Product>>>
        get() = _getListOfProducts


    fun getAdminListOfProducts() = viewModelScope.launch {
        FirebaseService.getAdminListOfProductFromFireStore().collect{
            _getListOfProducts.value = it
        }
    }

    fun getShopkeeperListOfProducts(uId: String) = viewModelScope.launch{
        FirebaseService.getShopkeeperListOfProductFromFireStore(uId).collect(){
            _getListOfProducts.value = it
        }
    }



}