package com.patriciajavier.pattyricetrading.firestore

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.firestore.models.*
import com.patriciajavier.pattyricetrading.firestore.models.Logs.Companion.toListOfLogs
import com.patriciajavier.pattyricetrading.firestore.models.Order.Companion.toListOfOrders
import com.patriciajavier.pattyricetrading.firestore.models.Product.Companion.toListOfProduct
import com.patriciajavier.pattyricetrading.firestore.models.Product.Companion.toProduct
import com.patriciajavier.pattyricetrading.firestore.models.ProductPerKg.Companion.toListOfProductPerKg
import com.patriciajavier.pattyricetrading.firestore.models.User.Companion.toListOfUsers
import com.patriciajavier.pattyricetrading.firestore.models.User.Companion.toUser
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
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

    suspend fun getAllUsers(): List<User>? {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("users")
                .get()
                .await().toListOfUsers()
        } catch (e: Exception) {
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
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user access rights", e)
            FirebaseCrashlytics.getInstance().log("Error getting user access rights")
            FirebaseCrashlytics.getInstance().setCustomKey("userId", uId)
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    suspend fun resetPassword(email: String){
        val db = FirebaseAuth.getInstance()
        try {
            db.sendPasswordResetEmail(email).addOnCompleteListener {
                if(it.isSuccessful)
                    Toast.makeText(MyApp.appContext, "Email Sent", Toast.LENGTH_SHORT).show()
            }.await()
        }catch (e:Exception){
            Log.e(TAG, "Error sending reset email", e)
            FirebaseCrashlytics.getInstance().log("Error getting user access rights")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun setAccountStatus(uId: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(uId)
        try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val newStatus = snapshot.getBoolean("isActive")!!

                if (newStatus) {
                    transaction.update(docRef, "isActive", false)
                } else {
                    transaction.update(docRef, "isActive", true)
                }
            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user status", e)
            FirebaseCrashlytics.getInstance().log("Error updating user status")
            FirebaseCrashlytics.getInstance().setCustomKey("userId", uId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


   suspend fun setNewUserInfo(newUserInfo: EditableUserInfo) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(newUserInfo.uId)
        try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)

                val userName = snapshot.getString("firstName")!!
                val lastName = snapshot.getString("lastName")!!
                val address = snapshot.getString("address")!!
                val phoneNumber = snapshot.getString("phoneNumber")!!

                if (userName != newUserInfo.firstName)
                    transaction.update(docRef, "firstName", newUserInfo.firstName)

                if (lastName != newUserInfo.lastName)
                    transaction.update(docRef, "lastName", newUserInfo.lastName)

                if (address != newUserInfo.address)
                    transaction.update(docRef, "address", newUserInfo.address)

                if (phoneNumber != newUserInfo.phoneNumber)
                    transaction.update(docRef, "phoneNumber", newUserInfo.phoneNumber)

            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user information", e)
            FirebaseCrashlytics.getInstance().log("Error updating user information")
            FirebaseCrashlytics.getInstance().setCustomKey("userId", newUserInfo.uId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    //-----------------------------------------------------------
    suspend fun addAdminProductToFireStore(productInfo: Product) {
        val db = FirebaseFirestore.getInstance()

        try {
            val imageUri =
                addImageToFirebaseStorage(productInfo.productImage.toUri(), productInfo.pId)

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

        } catch (e: Exception) {
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

    suspend fun updateAdminProductFromFireStore(productId: String, unitPrice: Double, stock: Int) {
        val db = FirebaseFirestore.getInstance()

        try {
            // Update field
            val data = hashMapOf(
                "unitPrice" to unitPrice,
                "stock" to stock,
            )

            db.collection("inventory").document(productId)
                .set(data, SetOptions.merge()).await()

        } catch (e: Exception) {
            Log.e(TAG, "Error updating stock", e)
            FirebaseCrashlytics.getInstance().log("Error updating product stock on fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("productId", productId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    suspend fun updateShopkeeperProductFromFireStore(productId: String, unitPrice: Double, uId: String) {
        val db = FirebaseFirestore.getInstance()

        try {
            // Update field
            val data = hashMapOf(
                "unitPrice" to unitPrice
            )

            db.collection("users")
                .document(uId)
                .collection("inventory")
                .document(productId)
                .set(data, SetOptions.merge()).await()

        } catch (e: Exception) {
            Log.e(TAG, "Error updating stock", e)
            FirebaseCrashlytics.getInstance().log("Error updating product stock on fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("productId", productId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    //-----------------------------------------------------------
    suspend fun deleteAdminProductFromFirestore(productId: String) = flow {
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)
            deleteImageToFirebaseStorage(productId)

            db.collection("inventory")
                .document(productId)
                .delete().await()

            emit(Response.Success("Successfully deleted"))
        } catch (e: Exception) {
            emit(Response.Failure(e))
            Log.e(TAG, "Error deleting product", e)
            FirebaseCrashlytics.getInstance().log("Error deleting product to fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("productId", productId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun deleteShopkeeperProductFromFirestore(productId: String, uId: String) = flow {
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)
            deleteImageToFirebaseStorage(productId)

            db.collection("users")
                .document(uId)
                .collection("inventory")
                .document(productId)
                .delete().await()

            emit(Response.Success("Successfully deleted"))
        } catch (e: Exception) {
            emit(Response.Failure(e))
            Log.e(TAG, "Error deleting product", e)
            FirebaseCrashlytics.getInstance().log("Error deleting product to fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("productId", productId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private suspend fun deleteImageToFirebaseStorage(productImageId: String) {
        val storageRef = FirebaseStorage.getInstance()
        try {
            storageRef.reference.child("ProductImage/${productImageId}")
                .delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting image", e)
            FirebaseCrashlytics.getInstance().log("Error deleting image to firebase cloud storage")
            FirebaseCrashlytics.getInstance().setCustomKey("imageUri", productImageId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
    //-----------------------------------------------------------


    suspend fun getAdminListOfProductFromFireStore() = flow {
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)
            val getListOfProduct = db.collection("inventory")
                .get().await().toListOfProduct()

            emit(Response.Success(getListOfProduct))
        } catch (e: Exception) {
            emit(Response.Failure(e))
            Log.e(TAG, "Error getting products", e)
            FirebaseCrashlytics.getInstance().log("Error getting products from fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", "")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun getShopkeeperListOfProductFromFireStore(uId: String) = flow {
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)
            val getListOfProduct = db.collection("users")
                .document(uId)
                .collection("inventory")
                .get().await().toListOfProduct()

            emit(Response.Success(getListOfProduct))
        } catch (e: Exception) {
            emit(Response.Failure(e))
            Log.e(TAG, "Error getting products", e)
            FirebaseCrashlytics.getInstance().log("Error getting products from fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", "")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun getAdminProductByIdFlow(productId: String) = flow {
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)

            val getProduct = db.collection("inventory")
                .document(productId)
                .get()
                .await().toProduct()

            emit(Response.Success(getProduct))
        } catch (e: Exception) {
            emit(Response.Failure(e))
            Log.e(TAG, "Error getting product info", e)
            FirebaseCrashlytics.getInstance().log("Error getting product info from fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", productId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun getShopkeeperProductByIdFlow(productId: String, uId: String) = flow {
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)

            val getProduct = db.collection("users")
                .document(uId)
                .collection("inventory")
                .document(productId)
                .get()
                .await().toProduct()

            emit(Response.Success(getProduct))
        } catch (e: Exception) {
            emit(Response.Failure(e))
            Log.e(TAG, "Error getting product info", e)
            FirebaseCrashlytics.getInstance().log("Error getting product info from fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", productId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private suspend fun getAdminProductById(productId: String): Product? {
        val db = FirebaseFirestore.getInstance()
        return try {

            db.collection("inventory")
                .document(productId)
                .get()
                .await().toProduct()

        } catch (e: Exception) {
            Log.e(TAG, "Error getting product info", e)
            FirebaseCrashlytics.getInstance().log("Error getting product info from fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", productId)
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }
    //-----------------------------------------------------------


    suspend fun setOrderRequest(orderInfo: Order) {
        val db = FirebaseFirestore.getInstance()
        try {

            val order = hashMapOf(
                "oId" to orderInfo.oId,
                "pId" to orderInfo.pId,
                "uId" to orderInfo.uId,
                "qTy" to orderInfo.qTy,
                "variation" to orderInfo.variation,
                "totalCost" to orderInfo.totalCost,
                "dateOrdered" to orderInfo.dateOrdered,
                "isAccepted" to orderInfo.isAccepted,
                "isDelivered" to orderInfo.isDelivered
            )

            db.collection("orders")
                .document(orderInfo.oId)
                .set(order).await()

        } catch (e: Exception) {
            Log.e(TAG, "Error order product", e)
            FirebaseCrashlytics.getInstance().log("Error ordering product")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", orderInfo.oId)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun getOrderRequest() = flow {
        val db = FirebaseFirestore.getInstance()

        try {
            emit(Response.Loading)

            val orderRequest = db.collection("orders")
                .get().await().toListOfOrders()

            val order = orderRequest!!.map {
                OrderCompleteInfo(
                    oId = it.oId,
                    pId = getAdminProductById(it.pId),
                    uId = getProfileData(it.uId),
                    qTy = it.qTy,
                    variation = it.variation,
                    totalCost = it.totalCost,
                    dateOrdered = it.dateOrdered,
                    isAccepted = it.isAccepted,
                    isDelivered = it.isDelivered
                )
            }

            emit(Response.Success(order))
        } catch (e: Exception) {
            emit(Response.Failure(e))
            Log.e(TAG, "Error getting orders", e)
            FirebaseCrashlytics.getInstance().log("Error getting orders")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", "orders")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    //-----------------------------------------------------------

    suspend fun deliverOrder(oId: String, pId: String, uId: String): Unit = coroutineScope{
        val db = FirebaseFirestore.getInstance()

        val dbRefOrders = db.collection("orders").document(oId)
        val dbRefInventory = db.collection("inventory").document(pId)
        val dbRefUser = db.collection("users").document(uId)

        try {
            db.runTransaction { transaction ->
                val orderSnapshot = transaction.get(dbRefOrders)
                val inventorySnapshot = transaction.get(dbRefInventory)
                val userSnapshot = transaction.get(dbRefUser)

                val qty = orderSnapshot.getLong("qTy")!!.toInt()
                val totalCost = orderSnapshot.getLong("totalCost")!!.toDouble()
                val variation = orderSnapshot.getLong("variation")!!.toInt()

                val stock = inventorySnapshot.getLong("stock")!!.toInt()
                val productImage = inventorySnapshot.getString("productImage")
                val productName = inventorySnapshot.getString("productName")
                val productDesc = inventorySnapshot.getString("productDesc")
                val kiloPerSack = inventorySnapshot.getLong("kiloPerSack")!!.toInt()
                val unitPrice = inventorySnapshot.getLong("unitPrice")!!.toDouble()

                val customerFirstName = userSnapshot.getString("firstName")
                val customerLastName = userSnapshot.getString("lastName")
                
                //check the current stock
                val validate = (stock - qty) > -1

                if(validate) {
                    //less the admin inventory
                    transaction.update(dbRefInventory, "stock", stock - qty)

                    val newProduct = mapOf(
                        "pId" to pId,
                        "productImage" to productImage,
                        "productName" to productName,
                        "productDesc" to productDesc,
                        "kiloPerSack" to kiloPerSack,
                        "unitPrice" to unitPrice
                    )

                    launch {
                        // add the ordered product in user inventory
                        db.collection("users").document(uId)
                            .collection("inventory").document(pId)
                            .set(newProduct, SetOptions.merge()).await()

                        //update the current stock or create if not exist.
                        db.collection("users").document(uId)
                            .collection("inventory").document(pId)
                            .update("stock", FieldValue.increment(qty.toLong())).await()
                    }


                    val newLog = mapOf(
                        "transactionId" to UUID.randomUUID().toString(),
                        "productName" to productName,
                        "customerName" to "$customerFirstName $customerLastName",
                        "timeCreated" to Timestamp.now().toDate(),
                        "quantity" to qty,
                        "totalCost" to totalCost,
                        "unitPrice" to unitPrice,
                        "variation" to variation,
                        "status" to "success",
                        "isFromMarket" to false
                    )

                    //log
                    launch {
                    db.collection("sales_report")
                        .document(oId)
                        .set(newLog)
                        .await()

                    db.collection("users")
                        .document(uId)
                        .collection("sales_report")
                        .document(oId)
                        .set(newLog)
                        .await()
                    }

                    transaction.delete(dbRefOrders)
                }
            }.await()
        } catch (e: Exception){
            Log.e(TAG, "Error transaction orders", e)
            FirebaseCrashlytics.getInstance().log("Error transaction orders")
            FirebaseCrashlytics.getInstance().setCustomKey("transactionOrder", "transaction")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    //-----------------------------------------------------------

    suspend fun declineOrder(oId: String, pId: String, uId: String): Unit = coroutineScope{
        val db = FirebaseFirestore.getInstance()
        val dbRefOrders = db.collection("orders").document(oId)
        val dbRefInventory = db.collection("inventory").document(pId)
        val dbRefUser = db.collection("users").document(uId)
        try {
            db.runTransaction { transaction ->
                val orderSnapshot = transaction.get(dbRefOrders)
                val inventorySnapshot = transaction.get(dbRefInventory)
                val userSnapshot = transaction.get(dbRefUser)

                val totalCost = orderSnapshot.getLong("totalCost")!!.toDouble()
                val variation = orderSnapshot.getLong("variation")!!.toInt()
                val qty = orderSnapshot.getLong("qTy")!!.toInt()
                val productName = inventorySnapshot.getString("productName")
                val unitPrice = inventorySnapshot.getLong("unitPrice")!!.toDouble()

                val customerFirstName = userSnapshot.getString("firstName")
                val customerLastName = userSnapshot.getString("lastName")

                val newLog = mapOf(
                    "transactionId" to UUID.randomUUID().toString(),
                    "productName" to productName,
                    "customerName" to "$customerFirstName $customerLastName",
                    "timeCreated" to Timestamp.now(),
                    "quantity" to qty,
                    "totalCost" to totalCost,
                    "unitPrice" to unitPrice,
                    "variation" to variation,
                    "status" to "cancelled",
                    "isFromMarket" to false
                )

                //log
                launch {
                    db.collection("sales_report")
                        .document(oId)
                        .set(newLog)
                        .await()
    
                    db.collection("users")
                        .document(uId)
                        .collection("sales_report")
                        .document(oId)
                        .set(newLog)
                        .await()
                }

                transaction.delete(dbRefOrders)

            }.await()
        }catch (e: Exception){
            Log.e(TAG, "Error cancelling transaction orders", e)
            FirebaseCrashlytics.getInstance().log("Error cancelling transaction orders")
            FirebaseCrashlytics.getInstance().setCustomKey("transactionOrder", "cancelling")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    //-----------------------------------------------------------


    suspend fun getAdminLogs() = flow{
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)

            val logs = db.collection("sales_report")
                .get().await().toListOfLogs()

            emit(Response.Success(logs))
        }catch (e: Exception) {
            emit(Response.Failure(e))
            Log.e(TAG, "Error getting logs", e)
            FirebaseCrashlytics.getInstance().log("Error getting logs")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", "logs")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun getShopkeeperLogs(uId: String) = flow{
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)

            val logs = db.collection("users")
                .document(uId)
                .collection("sales_report")
                .get().await().toListOfLogs()

            emit(Response.Success(logs))
        }catch (e: Exception) {
            emit(Response.Failure(e))
            Log.e(TAG, "Error getting logs", e)
            FirebaseCrashlytics.getInstance().log("Error getting logs")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", "logs")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    suspend fun sellProductThenLog(userId: String, product: Product): Unit = coroutineScope{
        val db = FirebaseFirestore.getInstance()

        val dbRefUserInventory = db.collection("users")
            .document(userId)
            .collection("inventory")
            .document(product.pId)


        try {
            db.runTransaction { transaction ->
                val userInventory = transaction.get(dbRefUserInventory)
                val stock = userInventory.getLong("stock")!!.toInt()

                if(stock < product.qty)
                    return@runTransaction

                val newStock = stock - product.qty

                transaction.update(dbRefUserInventory, "stock", newStock)

                val transactionID = UUID.randomUUID().toString()

                val newLog = mapOf(
                        "transactionId" to transactionID,
                        "productName" to product.productName,
                        "customerName" to "",
                        "timeCreated" to Timestamp.now().toDate(),
                        "quantity" to product.qty,
                        "totalCost" to product.qty * product.unitPrice,
                        "unitPrice" to product.unitPrice,
                        "variation" to product.kiloPerSack,
                        "status" to "success",
                        "isFromMarket" to product.isFromMarket
                    )

                    //log
                    launch {
                        db.collection("users")
                            .document(userId)
                            .collection("sales_report")
                            .document(transactionID)
                            .set(newLog)
                            .await()
                    }

            }.await()

        }catch (e: Exception){
            Log.e(TAG, "Error transaction sell products", e)
            FirebaseCrashlytics.getInstance().log("Error transaction sell products")
            FirebaseCrashlytics.getInstance().setCustomKey("transactionOrder", "sell products")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun sellProductPerKgThenLog(userId: String, product: ProductPerKg): Unit = coroutineScope{
        val db = FirebaseFirestore.getInstance()

        val dbRefUserInventory = db.collection("users")
            .document(userId)
            .collection("inventoryPerKg")
            .document(product.id)


        try {
            db.runTransaction { transaction ->
                val userInventory = transaction.get(dbRefUserInventory)
                val stock = userInventory.getLong("quantity")!!.toInt()

                if(stock < product.qty)
                    return@runTransaction

                val newStock = stock - product.qty

                transaction.update(dbRefUserInventory, "quantity", newStock)

                val transactionID = UUID.randomUUID().toString()

                val newLog = mapOf(
                    "transactionId" to transactionID,
                    "productName" to product.productName,
                    "customerName" to "",
                    "timeCreated" to Timestamp.now().toDate(),
                    "quantity" to product.qty,
                    "totalCost" to product.qty * product.unitPrice,
                    "unitPrice" to product.unitPrice,
                    "variation" to product.capacity,
                    "status" to "success",
                    "isFromMarket" to product.isFromMarket
                )

                //log
                launch {
                    db.collection("users")
                        .document(userId)
                        .collection("sales_report")
                        .document(transactionID)
                        .set(newLog)
                        .await()
                }

            }.await()

        }catch (e: Exception){
            Log.e(TAG, "Error transaction sell products", e)
            FirebaseCrashlytics.getInstance().log("Error transaction sell products")
            FirebaseCrashlytics.getInstance().setCustomKey("transactionOrder", "sell products")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }


    suspend fun saveProductPerKiloOnFireStore(product: Product, price: Double, userId: String){
        val db = FirebaseFirestore.getInstance()

        try {
            val newPerKgProduct = hashMapOf(
                "id" to product.pId,
                "productImage" to product.productImage,
                "productName" to product.productName,
                "quantity" to 0,
                "capacity" to 25,
                "unitPrice" to price,
            )

            db.collection("users")
                .document(userId)
                .collection("inventoryPerKg")
                .document(product.pId)
                .set(newPerKgProduct, SetOptions.merge()).await()

        }catch (e: Exception){
            Log.e(TAG, "Error transaction save products", e)
            FirebaseCrashlytics.getInstance().log("Error transaction save products")
            FirebaseCrashlytics.getInstance().setCustomKey("transactionOrder", "save products")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun getShopkeeperListOfProductPerKgFromFireStore(uId: String) = flow {
        val db = FirebaseFirestore.getInstance()
        try {
            emit(Response.Loading)
            val getListOfProduct = db.collection("users")
                .document(uId)
                .collection("inventoryPerKg")
                .get().await().toListOfProductPerKg()

            emit(Response.Success(getListOfProduct))
        } catch (e: Exception) {
            emit(Response.Failure(e))
            Log.e(TAG, "Error getting products", e)
            FirebaseCrashlytics.getInstance().log("Error getting products from fire store")
            FirebaseCrashlytics.getInstance().setCustomKey("GetProducts", "")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun refillProductPerKg(pId: String, uId: String): Unit = coroutineScope{
        val db = FirebaseFirestore.getInstance()

        val dbRefUserInventoryPerKg = db.collection("users").document(uId).collection("inventoryPerKg").document(pId)
        val dbRefUserInventory = db.collection("users").document(uId).collection("inventory").document(pId)

        try {
            db.runTransaction { transaction ->

                //less 1 sack to inventory
                //add the variation number of inventory to inventoryPerKg
                val inventory = transaction.get(dbRefUserInventory)

                val stock = inventory.getLong("stock")!!.toInt()
                val variation = inventory.getLong("kiloPerSack")

                if(stock <= 0){
                    Toast.makeText(MyApp.appContext, "This item is out of stock", Toast.LENGTH_SHORT).show()
                    return@runTransaction
                }

                val newStock = stock - 1

                transaction.update(dbRefUserInventory, "stock", newStock)
                transaction.update(dbRefUserInventoryPerKg, "quantity", variation)
            }.await()

        }catch (e: Exception){
            Log.e(TAG, "Error refilling", e)
            FirebaseCrashlytics.getInstance().log("Error refilling")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    suspend fun updateProductPerKgPrice(productId: String, uId: String, newPrice: Double){
        val db = FirebaseFirestore.getInstance()

        try {
            db.collection("users")
                .document(uId)
                .collection("inventoryPerKg")
                .document(productId)
                .update("unitPrice", newPrice).await()

        }catch (e: Exception){
            Log.e(TAG, "Error updating price", e)
            FirebaseCrashlytics.getInstance().log("Error updating price")
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}
