package com.patriciajavier.pattyricetrading.home.admin.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class ProductMarketViewModel : ViewModel(){

    private val _getListOfProducts = MutableLiveData<Response<List<Product>>>()
    val getListOfProducts : LiveData<Response<List<Product>>>
        get() = _getListOfProducts

    private val _cartContents = MutableLiveData<Product>()
    val cartContents : LiveData<Product>
        get() = _cartContents

//
//    private val eventChannel = Channel<Response<String>>()
//    val eventFLow = eventChannel.receiveAsFlow()

    fun getShopkeeperListOfProducts(uId: String) = viewModelScope.launch{
        FirebaseService.getShopkeeperListOfProductFromFireStore(uId).collect{
            _getListOfProducts.value = it
        }
    }

    fun sellProductToCustomer(userId: String, product: ConcurrentHashMap<String, Product>) = viewModelScope.launch{
        product.forEach {
            it.value.isFromMarket = true
            FirebaseService.sellProductThenLog(userId, it.value)
        }
    }

    fun addToCart(pId: String) {
       _getListOfProducts.value.apply {
          if(this is Response.Success){
              val product = this.data!!.find {
                   it.pId == pId
               }?.copy()
              _cartContents.value = product
           }
       }
    }

    fun clearCart(){
        _cartContents.value = null
    }
}