package com.patriciajavier.pattyricetrading.home.admin.inventory

import androidx.lifecycle.*
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.launch

class InventoryScreenViewModel : ViewModel() {

    private val _getListOfProductsMutableLiveData = MutableLiveData<InventoryState>()
    val getListOfProducts : LiveData<InventoryState> get() = _getListOfProductsMutableLiveData

    fun sortProducts(sortOptions: SortOptions = SortOptions.NAME){
        if (getListOfProducts.value != null){
            when(sortOptions){
                SortOptions.NAME -> {
                    val sortedProduct = _getListOfProductsMutableLiveData.value!!.product.sortedBy {
                            it.productName
                    }

                    val newState = InventoryState(
                        product = sortedProduct,
                    )

                    _getListOfProductsMutableLiveData.postValue(newState)
                }
                SortOptions.LOWEST_STOCK -> {
                    val sortedProduct = _getListOfProductsMutableLiveData.value!!.product.sortedBy{
                        it.stock
                    }

                    val newState = InventoryState(
                        product = sortedProduct
                    )

                    _getListOfProductsMutableLiveData.postValue(newState)
                }
                SortOptions.HIGHEST_STOCK -> {
                    val sortedProduct = _getListOfProductsMutableLiveData.value!!.product.sortedByDescending {
                        it.stock
                    }

                    val newState = InventoryState(
                        product = sortedProduct
                    )

                    _getListOfProductsMutableLiveData.postValue(newState)
                }
                SortOptions.LOWEST_PRICE -> {
                    val sortedProduct = _getListOfProductsMutableLiveData.value!!.product.sortedBy {
                        it.unitPrice
                    }

                    val newState = InventoryState(
                        product = sortedProduct
                    )

                    _getListOfProductsMutableLiveData.postValue(newState)
                }
                SortOptions.HIGHEST_PRICE -> {
                    val sortedProduct = _getListOfProductsMutableLiveData.value!!.product.sortedByDescending {
                        it.unitPrice
                    }

                    val newState = InventoryState(
                        product = sortedProduct
                    )

                    _getListOfProductsMutableLiveData.postValue(newState)
                }
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
)

enum class SortOptions{
    NAME, LOWEST_STOCK, HIGHEST_STOCK, LOWEST_PRICE, HIGHEST_PRICE;

    companion object{

        private const val name = "Name"
        private const val lowStock = "Low stock"
        private const val highStock = "High stock"
        private const val lowPrice = "Low price"
        private const val highPrice = "High price"

        fun getSortNames() : ArrayList<String> {

            val sortTitles = ArrayList<String>()

                for(item in values()){
                    when(item){
                        NAME -> sortTitles.add(name)
                        LOWEST_STOCK -> sortTitles.add(lowStock)
                        HIGHEST_STOCK -> sortTitles.add(highStock)
                        LOWEST_PRICE -> sortTitles.add(lowPrice)
                        HIGHEST_PRICE -> sortTitles.add(highPrice)
                    }
                }
            return sortTitles
        }

        fun getSelection(sortOption : String): SortOptions {

            when(sortOption){
                name -> return NAME
                lowStock -> return LOWEST_STOCK
                highStock -> return HIGHEST_STOCK
                lowPrice -> return LOWEST_PRICE
                highPrice -> return HIGHEST_PRICE
            }

            //default
            return NAME
        }

    }
}