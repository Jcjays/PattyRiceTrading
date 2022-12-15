package com.patriciajavier.pattyricetrading.home.admin.inventory

import androidx.lifecycle.*
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.launch

class InventoryScreenViewModel : ViewModel() {

    private val _getListOfProductsMutableLiveData = MutableLiveData<InventoryState>()
    val getListOfProducts : LiveData<InventoryState> get() = _getListOfProductsMutableLiveData

    fun sortProductsByName(sortOptions: SortOptions = SortOptions.NAME){
        if (getListOfProducts.value != null){
            when(sortOptions){
                SortOptions.NAME -> {
                    val sortedProduct = _getListOfProductsMutableLiveData.value!!.product.sortedBy {
                            it.productName
                    }

                    val newState = InventoryState(
                        product = sortedProduct,
                        sortOption = sortOptions
                    )

                    _getListOfProductsMutableLiveData.postValue(newState)
                }
                SortOptions.LOWEST_STOCK -> {}
                SortOptions.HIGHEST_STOCK -> {}
                SortOptions.LOWEST_PRICE -> {}
                SortOptions.HIGHEST_PRICE -> {}
            }
        }
    }

    fun getAdminListOfProducts() = viewModelScope.launch {
        FirebaseService.getAdminListOfProductFromFireStore().collect{
            when(it){
                Response.Loading -> _getListOfProductsMutableLiveData.postValue(
                    InventoryState(isLoadingDone = false)
                )
                is Response.Failure -> _getListOfProductsMutableLiveData.postValue(
                    InventoryState(errorMessage = it.e.message, isLoadingDone = true)
                )
                is Response.Success -> _getListOfProductsMutableLiveData.postValue(
                    InventoryState(product = it.data!!, isLoadingDone = true)
                )
            }
        }
    }

    fun getShopkeeperListOfProducts(uId: String) = viewModelScope.launch{
        FirebaseService.getShopkeeperListOfProductFromFireStore(uId).collect(){
            when(it){
                Response.Loading -> _getListOfProductsMutableLiveData.postValue(
                    InventoryState(isLoadingDone = false)
                )
                is Response.Failure -> _getListOfProductsMutableLiveData.postValue(
                    InventoryState(errorMessage = it.e.message, isLoadingDone = true)
                )
                is Response.Success -> _getListOfProductsMutableLiveData.postValue(
                    InventoryState(product = it.data!!, isLoadingDone = true)
                )
            }
        }
    }

}

data class InventoryState(
    val product : List<Product> = emptyList(),
    val errorMessage : String? = null,
    var isLoadingDone : Boolean = true,
    var sortOption : SortOptions = SortOptions.NAME
)

enum class SortOptions(){
    NAME, LOWEST_STOCK, HIGHEST_STOCK, LOWEST_PRICE, HIGHEST_PRICE
}