package com.patriciajavier.pattyricetrading.firestore.models

data class EditableUserInfo(
    val uId: String = "",
    val firstName: String = "",
    val lastName : String = "",
    val address: String = "",
    val phoneNumber: String = "",
)