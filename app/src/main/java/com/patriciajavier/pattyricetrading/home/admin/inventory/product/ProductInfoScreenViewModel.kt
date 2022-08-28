package com.patriciajavier.pattyricetrading.home.admin.inventory.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProductInfoScreenViewModel : ViewModel() {

    private val _getProductMutableLiveData = MutableLiveData<Response<Product>>()
    val getProductLiveData : LiveData<Response<Product>>
        get() = _getProductMutableLiveData

    private val _deleteProductMutableLiveData = MutableLiveData<Response<String>>()
    val deleteProductLiveData : LiveData<Response<String>>
        get() = _deleteProductMutableLiveData

    private val eventChannel = Channel<Response<String>>()
    val eventFLow = eventChannel.receiveAsFlow()

    fun getAdminProductFromFireStore(productId: String) = viewModelScope.launch {
        FirebaseService.getAdminProductByIdFlow(productId).collect{ product ->
            _getProductMutableLiveData.postValue(product)
        }
    }

    fun getShopkeeperProductFromFireStore(productId: String, uId: String) = viewModelScope.launch {
        FirebaseService.getShopkeeperProductByIdFlow(productId, uId).collect{ product ->
            _getProductMutableLiveData.postValue(product)
        }
    }

    fun updateAdminProductFromFireStore(productId: String, unitPrice: Double, stock: Int) = viewModelScope.launch {
        eventChannel.send(Response.Loading)

        try {
            FirebaseService.updateAdminProductFromFireStore(productId, unitPrice, stock)
            eventChannel.send(Response.Success("Update Success"))
        }catch (e: Exception){
            eventChannel.send(Response.Failure(e))
        }
    }

    fun updateShopkeeperProductFromFireStore(productId: String, unitPrice: Double, uId: String) = viewModelScope.launch {
        eventChannel.send(Response.Loading)

        try {
            FirebaseService.updateShopkeeperProductFromFireStore(productId, unitPrice, uId)
            eventChannel.send(Response.Success("Update Success"))
        }catch (e: Exception){
            eventChannel.send(Response.Failure(e))
        }
    }

    fun clearDeleteLiveData(){
        _deleteProductMutableLiveData.value = null
    }

    fun deleteAdminProduct(productId : String) = viewModelScope.launch {
        FirebaseService.deleteAdminProductFromFirestore(productId).collect{
            _deleteProductMutableLiveData.postValue(it)
        }
    }

    fun deleteShopkeeperProduct(productId : String, uId: String) = viewModelScope.launch {
        FirebaseService.deleteShopkeeperProductFromFirestore(productId, uId).collect{
            _deleteProductMutableLiveData.postValue(it)
        }
    }

}