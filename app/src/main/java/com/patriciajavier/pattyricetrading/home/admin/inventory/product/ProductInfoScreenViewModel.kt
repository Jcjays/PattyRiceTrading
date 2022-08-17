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

    fun getProductFromFireStore(productId: String) = viewModelScope.launch {
        FirebaseService.getProductFromFireStore(productId).collect{ product ->
            _getProductMutableLiveData.postValue(product)
        }
    }

    fun updateProductFromFireStore(productId: String, unitPrice: Double, stock: Int) = viewModelScope.launch {
        eventChannel.send(Response.Loading)

        try {
            FirebaseService.updateProductFromFireStore(productId, unitPrice, stock)
            eventChannel.send(Response.Success("Update Success"))
        }catch (e: Exception){
            eventChannel.send(Response.Failure(e))
        }
    }

    fun deleteProduct(productId : String) = viewModelScope.launch {
        FirebaseService.deleteProductToFirestore(productId).collect{
            _deleteProductMutableLiveData.postValue(it)
        }
    }

}