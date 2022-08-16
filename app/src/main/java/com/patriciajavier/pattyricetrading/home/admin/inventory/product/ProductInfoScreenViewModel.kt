package com.patriciajavier.pattyricetrading.home.admin.inventory.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ProductInfoScreenViewModel : ViewModel() {

    private val _getProduct = MutableLiveData<Response<Product>>()
    val getProduct : LiveData<Response<Product>>
        get() = _getProduct

    private val _deleteProduct = MutableLiveData<Response<String>>()
    val deleteProduct : LiveData<Response<String>>
        get() = _deleteProduct

    fun getProductFromFireStore(productId: String) = viewModelScope.launch {
        FirebaseService.getProductFromFireStore(productId).collect{ product ->
            _getProduct.postValue(product)
        }
    }

    fun deleteProduct(productId : String) = viewModelScope.launch {
        FirebaseService.deleteProductToFirestore(productId).collect{
            _deleteProduct.postValue(it)
        }
    }

}