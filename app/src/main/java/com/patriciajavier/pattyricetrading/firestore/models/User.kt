package com.patriciajavier.pattyricetrading.firestore.models

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot

data class User(
    val firstName: String = "",
    val lastName : String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = "",
    var isAdmin: Boolean = false,
){
    companion object{
        private const val TAG = "User"

        fun DocumentSnapshot.toUser() : User? {
            try {
                val firstName = getString("firstName")!!
                val lastName = getString("lastName")!!
                val address = getString("address")!!
                val phoneNumber = getString("phoneNumber")!!
                val email = getString("email")!!
                val password = getString("password")!!
                val isAdmin = getBoolean("isAdmin")!!

                return User(firstName, lastName, address, phoneNumber, email, password, isAdmin)
            } catch (e : Exception){
                Log.e(TAG, "Error converting user profile", e)
                FirebaseCrashlytics.getInstance().log("Error converting user profile")
                FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
    }
}
