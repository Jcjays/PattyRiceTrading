package com.patriciajavier.pattyricetrading.firestore.models

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

data class OrderCompleteInfo(
    val oId : String = UUID.randomUUID().toString(),
    val pId : Product? = Product(),
    val uId : User? = User(),
    val qTy : Int = 0,
    val variation: Int = 0,
    val totalCost: Double = 0.0,
    val dateOrdered: Date = Timestamp.now().toDate(),
    val isAccepted: Boolean = false,
    val isDelivered: Boolean = false,
)