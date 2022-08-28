package com.patriciajavier.pattyricetrading.registration.arch

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.firestore.models.User
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


object SharedRepository{
    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG = "RegistrationLoginRepository"

    var userMutableLiveData = MutableLiveData<DataOrException<FirebaseUser, Exception?>>()
    var isLoggedOutMutableLiveData = MutableLiveData<Boolean>()

    init {
        //initial check if theres a current user
        if(firebaseAuth.currentUser != null){
            userMutableLiveData.postValue(DataOrException(data = firebaseAuth.currentUser))
            isLoggedOutMutableLiveData.postValue(false)
        }
    }

        fun loginWithEmailPassword(email: String, password: String) {
            userMutableLiveData.postValue(DataOrException(isLoading = true))
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //user successfully logged in
                        userMutableLiveData.postValue(DataOrException(data = firebaseAuth.currentUser))
                        isLoggedOutMutableLiveData.postValue(false)
                    } else {
                        userMutableLiveData.postValue(DataOrException(exception = task.exception))
                        FirebaseCrashlytics.getInstance().log("Error logging in using firebase auth")
                        FirebaseCrashlytics.getInstance().setCustomKey("userEmail", email)
                        FirebaseCrashlytics.getInstance().recordException(task.exception as Exception)
                    }
                }
        }

      fun createAccountWithEmailPassword(userEntity: User){
            userMutableLiveData.postValue(DataOrException(isLoading = true))
            firebaseAuth.createUserWithEmailAndPassword(userEntity.email, userEntity.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        isLoggedOutMutableLiveData.postValue(false)
                        val currentUser = firebaseAuth.currentUser

                        //ready the data to be uploaded in firestore
                        val user = hashMapOf(
                            "uId" to currentUser!!.uid,
                            "firstName" to userEntity.firstName,
                            "lastName" to userEntity.lastName,
                            "address" to userEntity.address,
                            "phoneNumber" to userEntity.phoneNumber,
                            "email" to userEntity.email,
                            "password" to userEntity.password,
                            "isAdmin" to userEntity.isAdmin,
                            "isActive" to userEntity.isActive
                        )

                        fireStore.collection("users")
                            .document(currentUser.uid)
                            .set(user)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully written!")
                                Toast.makeText(MyApp.appContext, "Account successfully created", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                userMutableLiveData.postValue(DataOrException(exception = exception))
                                Log.e(TAG, "Error writing documents in firestore", exception)
                                FirebaseCrashlytics.getInstance().log("Error writing documents in firestore")
                                FirebaseCrashlytics.getInstance().setCustomKey("userId", currentUser.uid)
                                FirebaseCrashlytics.getInstance().recordException(exception)
                            }

                        userMutableLiveData.postValue(DataOrException(data = currentUser))

                    } else {
                        userMutableLiveData.postValue(DataOrException(exception = task.exception))
                        Log.e(TAG, task.exception?.message.toString())
                        FirebaseCrashlytics.getInstance().log(task.exception?.message.toString())
                    }
                }
        }

        fun logOut() {
            firebaseAuth.signOut()
            isLoggedOutMutableLiveData.postValue(true)
            userMutableLiveData.postValue(DataOrException(data = null))
        }
    }
