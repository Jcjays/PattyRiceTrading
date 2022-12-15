package com.patriciajavier.pattyricetrading.home.admin.inventory.order.restock

import androidx.lifecycle.*
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Order
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class OrderRequestViewModel : ViewModel(){

    private val _getProductMutableLiveData = MutableLiveData<Response<Product>>()
    val getProductLiveData : LiveData<Response<Product>>
        get() = _getProductMutableLiveData

    private val eventChannel = Channel<Response<String>>()
    val eventFLow = eventChannel.receiveAsFlow()


    fun getProductFromFireStore(productId: String) = viewModelScope.launch {
        FirebaseService.getAdminProductByIdFlow(productId).collect{ product ->
            _getProductMutableLiveData.value = product
        }
    }

    fun setOrderRequest(orderInfo: Order) = viewModelScope.launch {
        eventChannel.send(Response.Loading)
        try {
            FirebaseService.setOrderRequest(orderInfo)
            eventChannel.send(Response.Success("Order Placed!"))
        }catch (e: Exception){
            eventChannel.send(Response.Failure(e))
        }
    }


}