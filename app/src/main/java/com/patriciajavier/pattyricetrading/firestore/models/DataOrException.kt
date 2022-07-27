package com.patriciajavier.pattyricetrading.firestore.models

data class DataOrException<T, E : Exception?>(
    var data : T? = null,
    var exception: E? = null
)
