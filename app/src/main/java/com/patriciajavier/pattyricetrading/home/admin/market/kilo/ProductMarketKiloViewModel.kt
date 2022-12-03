package com.patriciajavier.pattyricetrading.home.admin.market.kilo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.ProductPerKg
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class ProductMarketKiloViewModel : ViewModel(){

    private val _getListOfProductsPerKg = MutableLiveData<Response<List<ProductPerKg>>>()
    val getListOfProductsPerKg get() = _getListOfProductsPerKg

    private val _cartContents = MutableLiveData<ProductPerKg>()
    val cartContents get() = _cartContents

    val mutableListOfProducts = MutableLiveData<List<Product>>()

    fun getShopkeeperListOfProducts(uId: String) = viewModelScope.launch{
        FirebaseService.getShopkeeperListOfProductFromFireStore(uId).collect{
            if(it is Response.Success){
                mutableListOfProducts.value = it.data
            }
        }
    }

    fun getShopkeeperListOfProductsPerKg(uId: String) = viewModelScope.launch{
        FirebaseService.getShopkeeperListOfProductPerKgFromFireStore(uId).collect{
               _getListOfProductsPerKg.value = it
        }
    }

    fun addToCart(pId: String) {
        _getListOfProductsPerKg.value.apply {
            if(this is Response.Success){
                val product = this.data!!.find {
                    it.id == pId
                }?.copy()
                _cartContents.value = product
            }
        }
    }


    fun clearCart(){
        _cartContents.value = null
    }

    fun sellProductToCustomer(userId: String, product: ConcurrentHashMap<String, ProductPerKg>) = viewModelScope.launch{
        product.forEach {
            FirebaseService.sellProductPerKgThenLog(userId, it.value)
        }
    }

    fun saveProductPerKg(product: Product, price : Double, userId: String) = viewModelScope.launch{
        FirebaseService.saveProductPerKiloOnFireStore(product, price, userId)
    }

    fun refillProductPerKg(pId : String, uId : String) = viewModelScope.launch{
        FirebaseService.refillProductPerKg(pId, uId)
    }

    fun updateProductPerKgPrice(pId: String, uId: String, newPrice: Double) = viewModelScope.launch {
        FirebaseService.updateProductPerKgPrice(pId, uId, newPrice)
    }

}