package com.patriciajavier.pattyricetrading.home.admin.inventory.order

import androidx.lifecycle.*
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Order
import com.patriciajavier.pattyricetrading.firestore.models.OrderCompleteInfo
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OrderModuleViewModel: ViewModel() {

    private val _getListOfProducts = MutableLiveData<Response<List<Product>>>()
    val getListOfProducts : LiveData<Response<List<Product>>>
        get() = _getListOfProducts

    private val _getListOfOrders = MutableLiveData<Response<List<OrderCompleteInfo>>>()
    val getListOfOrders : LiveData<Response<List<OrderCompleteInfo>>>
        get() = _getListOfOrders


    fun getAvailableListOfProducts() = viewModelScope.launch {
        FirebaseService.getAdminListOfProductFromFireStore().collect{
            _getListOfProducts.value = it
        }
    }

    fun getPendingOrdersOfProducts() = viewModelScope.launch {
        FirebaseService.getOrderRequest().collect{
            _getListOfOrders.value = it
        }
    }

    fun proceedOrderAndLog(oId: String, pId: String, uId: String) = viewModelScope.launch {
        FirebaseService.deliverOrder(oId, pId, uId)
    }

    fun declineOrderAndLog(oId: String, pId: String, uId: String) = viewModelScope.launch {
        FirebaseService.declineOrder(oId, pId, uId)
    }

}