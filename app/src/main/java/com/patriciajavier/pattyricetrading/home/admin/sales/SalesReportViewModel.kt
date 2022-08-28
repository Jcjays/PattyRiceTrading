package com.patriciajavier.pattyricetrading.home.admin.sales

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.Constant
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Logs
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.annotation.meta.When
import kotlin.collections.ArrayList

class SalesReportViewModel: ViewModel() {

    private val _logsMutableLiveData = MutableLiveData<Response<List<Logs>>>()

    private val _updateViewStateMutableLiveData = MutableLiveData<SalesSortedViewState>()
    val updateViewStateLiveData: LiveData<SalesSortedViewState> = _updateViewStateMutableLiveData

    fun getAdminLogs() = viewModelScope.launch {
        FirebaseService.getAdminLogs().collect{
            _logsMutableLiveData.value = it
            updateSalesViewState(it)
        }
    }

    fun getShopkeeperLogs(uId: String) = viewModelScope.launch {
        FirebaseService.getShopkeeperLogs(uId).collect {
            _logsMutableLiveData.value = it
            updateSalesViewState(it)
        }
    }

    var currentSort: SalesSortedViewState.Sort = SalesSortedViewState.Sort.DAILY
    set(value) {
        field = value
        updateSalesViewState(_logsMutableLiveData.value!!)
    }

    private fun updateSalesViewState(response: Response<List<Logs>>) {
        val modifiedList: ArrayList<Logs> = ArrayList()//container for modified data
        when(response){
            is Response.Loading -> _updateViewStateMutableLiveData.postValue(SalesSortedViewState(isLoading = true))
            is Response.Failure -> _updateViewStateMutableLiveData.postValue(SalesSortedViewState(exception = response.e))
            is Response.Success -> {
                when(currentSort){
                    SalesSortedViewState.Sort.DAILY ->{
                        response.data!!.sortedByDescending { it.timeCreated }.forEach {
                            if(Constant.getCalculatedDate("yyyyMMdd", 0).toInt()
                                == Constant.timeStampToGMT8(it.timeCreated, "yyyyMMdd").toInt()){
                                modifiedList.add(it)
                            }
                        }
                        _updateViewStateMutableLiveData.postValue(SalesSortedViewState(data = modifiedList))
                    }
                    SalesSortedViewState.Sort.WEEKLY ->{
                        response.data!!.sortedByDescending { it.timeCreated }.forEach {
                            //get the data that dates back within 7 days.
                            if(Constant.getCalculatedDate("yyyyMMdd", -7).toInt()
                                <= Constant.timeStampToGMT8(it.timeCreated, "yyyyMMdd").toInt()){
                                modifiedList.add(it)
                            }
                        }
                        _updateViewStateMutableLiveData.postValue(SalesSortedViewState(data = modifiedList))
                    }
                    SalesSortedViewState.Sort.MONTHLY ->{
                        response.data!!.sortedByDescending { it.timeCreated }.forEach {
                            //get the data that dates back within 30 days.
                            if(Constant.getCalculatedDate("yyyyMMdd", -30).toInt()
                                <= Constant.timeStampToGMT8(it.timeCreated, "yyyyMMdd").toInt()){
                                modifiedList.add(it)
                            }
                        }
                        _updateViewStateMutableLiveData.postValue(SalesSortedViewState(data = modifiedList))
                    }
                    SalesSortedViewState.Sort.ANNUALLY ->{
                        response.data!!.sortedByDescending { it.timeCreated }.forEach {
                            //get the data that dates back within 30 days.
                            if(Constant.getCalculatedDate("yyyyMMdd", -365).toInt()
                                <= Constant.timeStampToGMT8(it.timeCreated, "yyyyMMdd").toInt()){
                                modifiedList.add(it)
                            }
                        }
                        _updateViewStateMutableLiveData.postValue(SalesSortedViewState(data = modifiedList))

                    }
                    SalesSortedViewState.Sort.LIFETIME ->{
                        response.data!!.sortedByDescending { it.timeCreated }.forEach {
                            //get the data that dates back within 30 days.
                            if(Constant.getCalculatedDate("yyyyMMdd", -99999).toInt()
                                <= Constant.timeStampToGMT8(it.timeCreated, "yyyyMMdd").toInt()){
                                modifiedList.add(it)
                            }
                        }
                        _updateViewStateMutableLiveData.postValue(SalesSortedViewState(data = modifiedList))
                    }
                }
            }
        }
    }

    data class SalesSortedViewState(
       val isLoading: Boolean = false,
       val data: List<Logs> = emptyList(),
       val exception: Exception? = null,
       val sort: Sort = Sort.DAILY
    ){
       enum class Sort{
           DAILY,
           WEEKLY,
           MONTHLY,
           ANNUALLY,
           LIFETIME
       }
    }
}