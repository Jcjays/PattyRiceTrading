package com.patriciajavier.pattyricetrading.firestore

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.patriciajavier.pattyricetrading.firestore.models.*
import com.patriciajavier.pattyricetrading.firestore.models.Product.Companion.toListOfProduct
import com.patriciajavier.pattyricetrading.firestore.models.Product.Companion.toProduct
import com.patriciajavier.pattyricetrading.firestore.models.User.Companion.toListOfUsers
import com.patriciajavier.pattyricetrading.firestore.models.User.Companion.toUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

object FirebaseService {
    private const val TAG = "FirebaseService"

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

    suspend fun getAllUsers() : List<User>?{
        val db = FirebaseFirestore.getInstance()
        return try {
         db.collection("users")
                .get()
                .await().toListOfUsers()
        }catch (e: Exception){
            Log.e(TAG, "Error fetching all the users", e)
            FirebaseCrashlytics.getInstance().log("Error getting users")
            FirebaseCrashlytics.getInstance().setCustomKey("ListOfUsers", "getAllUsers")
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

    fun setAccountStatus(uId: String){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(uId)
        try{
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val newStatus = snapshot.getBoolean("isActive")!!

                if(newStatus){
                    transaction.update(docRef, "isActive", false)
                }else{
                    transaction.update(docRef, "isActive", true)
                }
            }
        }catch (e : Exception){
            Log.e(TAG, "Error updating user status", e)
            FirebaseCrashlytics.getInstance().log("Error updating user status")
            FirebaseCrashlytics.getInstance().setCustomKey("userId", uId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    fun setNewUserInfo(newUserInfo : EditableUserInfo){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(newUserInfo.uId)
        try{
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)

                val userName = snapshot.getString("firstName")!!
                val lastName = snapshot.getString("lastName")!!
                val address = snapshot.getString("address")!!
                val phoneNumber = snapshot.getString("phoneNumber")!!

                if(userName != newUserInfo.firstName)
                    transaction.update(docRef, "firstName", newUserInfo.firstName)

                if(lastName != newUserInfo.lastName)
                    transaction.update(docRef, "lastName", newUserInfo.lastName)

                if(address != newUserInfo.address)
                    transaction.update(docRef, "address", newUserInfo.address)

                if(phoneNumber != newUserInfo.phoneNumber)
                    transaction.update(docRef, "phoneNumber", newUserInfo.phoneNumber)

            }
        }catch (e : Exception){
            Log.e(TAG, "Error updating user information", e)
            FirebaseCrashlytics.getInstance().log("Error updating user information")
            FirebaseCrashlytics.getInstance().setCustomKey("userId", newUserInfo.uId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    //check if document exist
    //upload product photo to firestore cloud
    //get the product reference from firestore cloud
    //upload the product info to firestore

    //-----------------------------------------------------------
    suspend fun addUpdateProductToFireStore(productInfo: Product){
        val db = FirebaseFirestore.getInstance()

        try {
            val imageUri = addImageToFirebaseStorage(productInfo.productImage.toUri(), productInfo.pId)

            val newProduct = hashMapOf(
                "pId" to productInfo.pId,
                "productImage" to imageUri,
                "productName" to productInfo.productName,
                "productDesc" to productInfo.productDesc,
                "kiloPerSack" to productInfo.kiloPerSack,
                "stock" to productInfo.stock,
                "unitPrice" to productInfo.unitPrice
            )

            db.collection("inventory")
                .document(productInfo.pId)
                .set(newProduct, SetOptions.merge()).await()

        }catch (e: Exception){
            Log.e(TAG, "Error uploading/updating new product", e)
            FirebaseCrashlytics.getInstance().log("Error uploading/updating product to fire store.")
            FirebaseCrashlytics.getInstance().setCustomKey("productId", productInfo.pId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private suspend fun addImageToFirebaseStorage(imageUri: Uri, productId: String): Uri? {
        val storageRef = FirebaseStorage.getInstance()
        return try {
            //uploading image and getting its reference
            storageRef.reference.child("ProductImage/${productId}")
                .putFile(imageUri).await()
                .storage.downloadUrl.await()

        } catch (e: Exception) {
            Log.e(TAG, "Error uploading image", e)
            FirebaseCrashlytics.getInstance().log("Error uploading image to firebase cloud storage")
            FirebaseCrashlytics.getInstance().setCustomKey("imageUri", imageUri.toString())
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }
    //-----------------------------------------------------------

    suspend fun updateProductFromFireStore(productId: String, unitPrice: Double, stock : Int){
        val db = FirebaseFirestore.getInstance()

        try {
            // Update field
            val data = hashMapOf(
                "unitPrice" to unitPrice,
                "stock" to stock,
            )

            db.collection("inventory").document(productId)
                .set(data, SetOptions.merge()).await()

        }catch (e : Exception){
            Log.e(TAG, "Error updating stock", e)
            FirebaseCrashlytics.getInstance().log("Error updating product stock on fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("productId", productId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    //-----------------------------------------------------------
    suspend fun deleteProductToFirestore(productId: String) = flow{
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)
            deleteImageToFirebaseStorage(productId)

            db.collection("inventory")
                .document(productId)
                .delete().await()

            emit(Response.Success("Successfully deleted"))
        }catch (e: Exception){
            emit(Response.Failure(e))
            Log.e(TAG, "Error deleting product", e)
            FirebaseCrashlytics.getInstance().log("Error deleting product to fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("productId", productId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private suspend fun deleteImageToFirebaseStorage(productImageId: String){
        val storageRef = FirebaseStorage.getInstance()
        try {
            storageRef.reference.child("ProductImage/${productImageId}")
                .delete().await()
        }catch (e: Exception){
            Log.e(TAG, "Error deleting image", e)
            FirebaseCrashlytics.getInstance().log("Error deleting image to firebase cloud storage")
            FirebaseCrashlytics.getInstance().setCustomKey("imageUri", productImageId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
    //-----------------------------------------------------------


   suspend fun getListOfProductFromFireStore() = flow{
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)
            val getListOfProduct = db.collection("inventory")
                .get().await().toListOfProduct()

            emit(Response.Success(getListOfProduct))
        }catch (e: Exception){
            emit(Response.Failure(e))
            Log.e(TAG, "Error getting products", e)
            FirebaseCrashlytics.getInstance().log("Error getting products from fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", "")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun getProductFromFireStore(productId : String) = flow {
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)

            val getProduct = db.collection("inventory")
                .document(productId)
                .get()
                .await().toProduct()

            emit(Response.Success(getProduct))
        }catch (e : Exception){
            emit(Response.Failure(e))
            Log.e(TAG, "Error getting product info", e)
            FirebaseCrashlytics.getInstance().log("Error getting product info from fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", productId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }

    }
}
