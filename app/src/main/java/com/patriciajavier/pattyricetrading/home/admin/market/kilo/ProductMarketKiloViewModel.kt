package com.patriciajavier.pattyricetrading.home.admin.market.kilo

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.ProductPerKg
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProductMarketKiloViewModel : ViewModel(){

    private val _getListOfProductsPerKg = MutableLiveData<Response<List<ProductPerKg>>>()
    val getListOfProductsPerKg : LiveData<Response<List<ProductPerKg>>>
        get() = _getListOfProductsPerKg

    private val _cartContents = MutableLiveData<ProductPerKg>()
    val cartContents : LiveData<ProductPerKg>
        get() = _cartContents

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

    fun sellProductToCustomer(userId: String, product: LinkedHashMap<String, ProductPerKg>) = viewModelScope.launch{
        product.forEach {
            FirebaseService.sellProductPerKgThenLog(userId, it.value)
        }
        Toast.makeText(MyApp.appContext, "Transaction success", Toast.LENGTH_SHORT).show()
    }

    fun saveProductPerKg(product: Product, price : Double, userId: String) = viewModelScope.launch{
        FirebaseService.saveProductPerKiloOnFireStore(product, price, userId)
    }


    fun refillProductPerKg(pId : String, uId : String) = viewModelScope.launch{
        FirebaseService.refillProductPerKg(pId, uId)
    }

}