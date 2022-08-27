package com.patriciajavier.pattyricetrading.firestore.models

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

data class Logs(
    val transactionId: String = "",
    val customerName : String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val timeCreated: Timestamp = Timestamp.now(),
    val totalCost: Double = 0.0,
    val unitPrice: Double = 0.0,
    val variation: Int = 0,
    val isFromMarket: Boolean = false
){
    companion object{
        private const val TAG = "Logs"

       private fun DocumentSnapshot.toLog(): Logs?{
            try {

                if(data.isNullOrEmpty()){
                    return null
                }

                val transactionId = getString("transactionId")!!
                val customerName = getString("customerName")!!
                val productName = getString("productName")!!
                val quantity = getLong("quantity")!!.toInt()
                val timeCreated = getTimestamp("timeCreated")!!
                val totalCost = getLong("totalCost")!!.toDouble()
                val unitPrice = getLong("unitPrice")!!.toDouble()
                val variation = getLong("variation")!!.toInt()
                val isFromMarket = getBoolean("isFromMarket")!!

                return Logs(transactionId, customerName, productName, quantity, timeCreated, totalCost, unitPrice, variation, isFromMarket)
            }catch (e: Exception){
                Log.e(TAG, "Error converting product", e)
                FirebaseCrashlytics.getInstance().log("Error converting product")
                FirebaseCrashlytics.getInstance().setCustomKey("productId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
    fun QuerySnapshot.toListOfLogs() : List<Logs>?{
        return try{
            val list : ArrayList<Logs> = ArrayList()

            documents.forEach {
                it.toLog()?.let { it1 -> list.add(it1) }
            }

            list
        }catch (e: Exception){
            Log.e(TAG, "Error fetching all the products", e)
            FirebaseCrashlytics.getInstance().log("Error fetching all the products profile")
            FirebaseCrashlytics.getInstance().setCustomKey("ToListOfProducts", "listOfProducts")
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }
    }
}