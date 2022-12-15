package com.patriciajavier.pattyricetrading.firestore.models

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.patriciajavier.pattyricetrading.firestore.models.User.Companion.toUser

data class Product(
    val pId : String = "",
    val productImage: String = "",
    val productName: String = "",
    val productDesc: String = "",
    val kiloPerSack: Int = 0,
    val stock: Int = 0,
    val timeAdded: Timestamp = Timestamp.now(),
    val unitPrice: Double = 0.0,
    var qty: Int = 0,
    var isFromMarket: Boolean = false,
){
    companion object{
        private const val TAG = "Product"

        fun DocumentSnapshot.toProduct() : Product?{
            try {

                if(data.isNullOrEmpty()){
                    return null
                }

                val pId = getString("pId")!!
                val productName = getString("productName")!!
                val productDesc = getString("productDesc")!!
                val stock = getLong("stock")!!.toInt()
                val timeAdded = getTimestamp("timeAdded")!!
                val kiloPerSack = getLong("kiloPerSack")!!.toInt()
                val unitPrice = getDouble("unitPrice")!!
                val productImage = getString("productImage")!!

                return Product(pId, productImage, productName, productDesc, kiloPerSack, stock,timeAdded, unitPrice)
            }catch (e :Exception){
                Log.e(TAG, "Error converting product", e)
                FirebaseCrashlytics.getInstance().log("Error converting product")
                FirebaseCrashlytics.getInstance().setCustomKey("productId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }

        fun QuerySnapshot.toListOfProduct() : List<Product>?{
            try{
                val list : ArrayList<Product> = ArrayList()

                documents.forEach {
                    it.toProduct()?.let { it1 -> list.add(it1) }
                }

                return list

            }catch (e: Exception){
                Log.e(TAG, "Error fetching all the products", e)
                FirebaseCrashlytics.getInstance().log("Error fetching all the products profile")
                FirebaseCrashlytics.getInstance().setCustomKey("ToListOfProducts", "listOfProducts")
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
    }
}