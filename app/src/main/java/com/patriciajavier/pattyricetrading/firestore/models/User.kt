package com.patriciajavier.pattyricetrading.firestore.models

data class User(
    val firstName: String = "",
    val lastName : String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = "",
    var isCreated: Boolean = false,
    var isNew : Boolean = false
)
