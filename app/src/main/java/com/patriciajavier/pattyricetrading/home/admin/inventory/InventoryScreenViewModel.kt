package com.patriciajavier.pattyricetrading.home.admin.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Logs
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import com.patriciajavier.pattyricetrading.home.admin.sales.SalesReportViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class InventoryScreenViewModel : ViewModel() {

    private val _getListOfProducts = MutableLiveData<Response<List<Product>>>()
    val getListOfProducts: LiveData<Response<List<Product>>>
        get() = _getListOfProducts

    //dinagdag ko
    private val _updateViewStateMutableLiveData = MutableLiveData<InventoryScreenViewModel.InventorySortedViewState>()
    val updateViewStateLiveData: LiveData<InventoryScreenViewModel.InventorySortedViewState> = _updateViewStateMutableLiveData

    fun getAdminListOfProducts() = viewModelScope.launch {
        FirebaseService.getAdminListOfProductFromFireStore().collect {
            _getListOfProducts.value = it
            updateInventoryViewState(it)

        }
    }

    fun getShopkeeperListOfProducts(uId: String) = viewModelScope.launch {
        FirebaseService.getShopkeeperListOfProductFromFireStore(uId).collect() {
            _getListOfProducts.value = it
            updateInventoryViewState(it)
        }
    }

    // for sorting dinagdag ko lang sana makatulong po
    var currentSort: InventoryScreenViewModel.InventorySortedViewState.Sort =
        InventoryScreenViewModel.InventorySortedViewState.Sort.New
        set(value) {
            field = value
            updateInventoryViewState(_getListOfProducts.value!!)
        }

    private fun updateInventoryViewState(response: Response<List<Product>>) {
        val modifiedList: ArrayList<Product> = ArrayList()//container for modified data
        when (response) {
            is Response.Loading -> _updateViewStateMutableLiveData.postValue(
                InventoryScreenViewModel.InventorySortedViewState(isLoading = true)
            )
            is Response.Failure -> _updateViewStateMutableLiveData.postValue(
                InventoryScreenViewModel.InventorySortedViewState(exception = response.e)
            )
            is Response.Success -> {
                when (currentSort) {
                    InventoryScreenViewModel.InventorySortedViewState.Sort.New -> {

                        _updateViewStateMutableLiveData.postValue(
                            InventoryScreenViewModel.InventorySortedViewState(data = modifiedList)
                        )
                    }
                    InventoryScreenViewModel.InventorySortedViewState.Sort.Popular -> {


                        _updateViewStateMutableLiveData.postValue(
                            InventoryScreenViewModel.InventorySortedViewState(
                                data = modifiedList
                            )
                        )
                    }
                }
            }
        }
    }


    data class InventorySortedViewState(
        val isLoading: Boolean = false,
        val data: List<Product> = emptyList(),
        val exception: Exception? = null,
        val sort: Sort = Sort.New
    ) {
        enum class Sort {
            New,
            Popular
        }

    }
}