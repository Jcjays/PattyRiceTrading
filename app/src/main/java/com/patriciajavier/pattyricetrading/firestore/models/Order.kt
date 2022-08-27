package com.patriciajavier.pattyricetrading.firestore.models

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.patriciajavier.pattyricetrading.firestore.models.Product.Companion.toProduct
import java.util.*


data class Order(
    val oId : String = UUID.randomUUID().toString(),
    val pId : String = "",
    val uId : String = "",
    val qTy : Int = 0,
    val variation: Int = 0,
    val totalCost: Double = 0.0,
    val dateOrdered: Date = Timestamp.now().toDate(),
    val isAccepted: Boolean = false,
    val isDelivered: Boolean = false,
) {
    companion object {
        private const val TAG = "Order"
        private fun DocumentSnapshot.toOrder(): Order? {
            try {
                val oId = getString("oId")!!
                val pId = getString("pId")!!
                val uId = getString("uId")!!
                val qTy = getLong("qTy")!!.toInt()
                val variation = getLong("variation")!!.toInt()
                val totalCost = getLong("totalCost")!!.toDouble()
                val dateOrdered = getDate("dateOrdered")!!
                val isDelivered = getBoolean("isDelivered")!!

                return Order(
                    oId,
                    pId,
                    uId,
                    qTy,
                    variation,
                    totalCost,
                    dateOrdered,
                    isDelivered
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error converting product", e)
                FirebaseCrashlytics.getInstance().log("Error converting product")
                FirebaseCrashlytics.getInstance().setCustomKey("productId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }

        fun QuerySnapshot.toListOfOrders() : List<Order>?{
            try{
                val list : ArrayList<Order> = ArrayList()
                documents.forEach { list.add(it.toOrder()!!) }
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