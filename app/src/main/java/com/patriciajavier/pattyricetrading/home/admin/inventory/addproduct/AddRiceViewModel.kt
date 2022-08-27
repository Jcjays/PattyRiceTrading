package com.patriciajavier.pattyricetrading.home.admin.inventory.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patriciajavier.pattyricetrading.firestore.FirebaseService
import com.patriciajavier.pattyricetrading.firestore.models.Product
import com.patriciajavier.pattyricetrading.firestore.models.Response
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddRiceViewModel : ViewModel() {

    private val eventChannel = Channel<Response<String>>()
    val eventFLow = eventChannel.receiveAsFlow()


    fun addAdminProductToFireStore(productInfo: Product) = viewModelScope.launch{
        eventChannel.send(Response.Loading)
        try {
            FirebaseService.addAdminProductToFireStore(productInfo)
            eventChannel.send(Response.Success("Upload Success"))
        }catch (e: Exception){
            eventChannel.send(Response.Failure(e))
        }
    }

//    fun addShopkeeperProductToFireStore(productInfo: Product, uid: String) = viewModelScope.launch{
//        eventChannel.send(Response.Loading)
//        try {
//            FirebaseService.addShopkeeperProductToFireStore(productInfo, uid)
//            eventChannel.send(Response.Success("Upload Success"))
//        }catch (e: Exception){
//            eventChannel.send(Response.Failure(e))
//        }
//    }
}