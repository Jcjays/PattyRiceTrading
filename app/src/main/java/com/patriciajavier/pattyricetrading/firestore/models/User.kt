package com.patriciajavier.pattyricetrading.firestore.models

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.patriciajavier.pattyricetrading.firestore.models.User.Companion.toListOfUsers

data class User(
    val uId: String = "",
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
                val uId = getString("uId")!!
                val firstName = getString("firstName")!!
                val lastName = getString("lastName")!!
                val address = getString("address")!!
                val phoneNumber = getString("phoneNumber")!!
                val email = getString("email")!!
                val password = getString("password")!!
                val isAdmin = getBoolean("isAdmin")!!

                return User(uId, firstName, lastName, address, phoneNumber, email, password, isAdmin)
            } catch (e : Exception){
                Log.e(TAG, "Error converting user profile", e)
                FirebaseCrashlytics.getInstance().log("Error converting user profile")
                FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }

        fun QuerySnapshot.toListOfUsers(): List<User>?{
            try{
                val list : ArrayList<User> = ArrayList()
                documents.forEach { list.add(it.toUser()!!) }
                return list
            }catch (e: Exception){
                Log.e(TAG, "Error fetching all the user profile", e)
                FirebaseCrashlytics.getInstance().log("Error fetching all the user profile")
                FirebaseCrashlytics.getInstance().setCustomKey("ToListOfUsers", "listOfUser")
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
    }
}
