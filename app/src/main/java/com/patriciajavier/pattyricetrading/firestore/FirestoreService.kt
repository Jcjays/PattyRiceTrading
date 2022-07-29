package com.patriciajavier.pattyricetrading.firestore

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.patriciajavier.pattyricetrading.firestore.models.User
import com.patriciajavier.pattyricetrading.firestore.models.User.Companion.toUser
import kotlinx.coroutines.tasks.await

object FirestoreService {
    private const val TAG = "FirestoreService"

    suspend fun getProfileData(uId: String): User? {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("users")
                .document(uId)
                .get().await().toUser()
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user details", e)
            FirebaseCrashlytics.getInstance().log("Error getting user details")
            FirebaseCrashlytics.getInstance().setCustomKey("userId", uId)
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    suspend fun checkIfAdmin(uId: String): Boolean? {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("users")
                .document(uId)
                .get()
                .await().data!!["isAdmin"] as Boolean
        }catch (e : Exception){
            Log.e(TAG, "Error fetching user access rights", e)
            FirebaseCrashlytics.getInstance().log("Error getting user access rights")
            FirebaseCrashlytics.getInstance().setCustomKey("userId", uId)
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

}