package com.patriciajavier.pattyricetrading.firestore.models

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

data class ProductPerKg(
    val id: String = "",
    val productImage : String = "",
    val productName : String = "",
    val quantity : Int = 0,
    val capacity: Int = 0,
    val unitPrice: Double = 0.0,
    var qty: Int = 0,
    var isFromMarket: Boolean = true
) {
    companion object{
        const val TAG = "ProductPerKg"

        fun DocumentSnapshot.toProductPerKg() : ProductPerKg?{
            try {

                val id = getString("id")!!
                val productImage = getString("productImage")!!
                val productName = getString("productName")!!
                val quantity = getLong("quantity")!!.toInt()
                val capacity = getLong("capacity")!!.toInt()
                val unitPrice = getLong("unitPrice")!!.toDouble()

                return ProductPerKg(id, productImage, productName, quantity, capacity, unitPrice)
            }catch (e : Exception){
                Log.e(TAG, "Error converting productPerKg", e)
                FirebaseCrashlytics.getInstance().log("Error converting productPerKg")
                FirebaseCrashlytics.getInstance().setCustomKey("productId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }

        fun QuerySnapshot.toListOfProductPerKg() : List<ProductPerKg>?{
            try {
                val list : ArrayList<ProductPerKg> = ArrayList()

                documents.forEach {
                    it.toProductPerKg()?.let { it1 -> list.add(it1) }
                }

                return list
            }catch (e: Exception){
                Log.e(TAG, "Error converting productPerKg", e)
                FirebaseCrashlytics.getInstance().log("Error fetching all the productsPerKg")
                FirebaseCrashlytics.getInstance().setCustomKey("ToListOfProducts", "listOfProducts")
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
    }
}