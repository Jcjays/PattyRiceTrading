package com.patriciajavier.pattyricetrading.registration

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.patriciajavier.pattyricetrading.firestore.models.DataOrException
import com.patriciajavier.pattyricetrading.firestore.models.User

class RegistrationLoginRepository {

    private val TAG = "RegistrationLoginRepository"
    private var firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    var userMutableLiveData = MutableLiveData<DataOrException<FirebaseUser, Exception?>>()
    var isLoggedOutMutableLiveData = MutableLiveData<Boolean>()
    var isLoading = MutableLiveData<Boolean>()

    init {
        if(firebaseAuth.currentUser != null){
            userMutableLiveData.postValue(DataOrException(firebaseAuth.currentUser))
            isLoggedOutMutableLiveData.postValue(false)
        }
    }

    fun loginWithEmailPassword(email: String, password: String){
        isLoading.postValue(true)
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    userMutableLiveData.postValue(DataOrException(firebaseAuth.currentUser))
                    isLoggedOutMutableLiveData.postValue(false)
                }else{
                    userMutableLiveData.postValue(DataOrException(exception = task.exception))
                    FirebaseCrashlytics.getInstance().log("Error logging in using firebase auth")
                    FirebaseCrashlytics.getInstance().setCustomKey("userEmail", email)
                    FirebaseCrashlytics.getInstance().recordException(task.exception as Exception)
                }
                isLoading.postValue(false)
            }
    }

    fun createAccountWithEmailPassword(userEntity : User){
        isLoading.postValue(true)
        firebaseAuth.createUserWithEmailAndPassword(userEntity.email, userEntity.password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.d(TAG, "createUserWithEmail:success")

                    val currentUser = firebaseAuth.currentUser

                    //ready the extra data to be uploaded in firestore
                    val user = hashMapOf(
                        "firstName" to userEntity.firstName,
                        "lastName" to userEntity.lastName,
                        "address" to userEntity.address,
                        "phoneNumber" to userEntity.phoneNumber,
                        "email" to userEntity.email,
                        "password" to userEntity.password,
                    )

                    firestore.collection("users")
                        .document(firebaseAuth.currentUser!!.uid) //setting the id as the userID
                        .set(user)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully written!")
                            userMutableLiveData.postValue(DataOrException(currentUser))
                            isLoggedOutMutableLiveData.postValue(false)
                        }
                        .addOnFailureListener { exception ->
                            userMutableLiveData.postValue(DataOrException(exception = exception))
                            Log.e(TAG, "Error writing documents in firestore", exception)
                            FirebaseCrashlytics.getInstance().log("Error writing documents in firestore")
                            FirebaseCrashlytics.getInstance().setCustomKey("userId", currentUser!!.uid)
                            FirebaseCrashlytics.getInstance().recordException(exception)
                        }

                }else{
                    userMutableLiveData.postValue(DataOrException(exception = task.exception))
                    Log.e(TAG, task.exception?.message.toString())
                    FirebaseCrashlytics.getInstance().log(task.exception?.message.toString())
                }
                isLoading.postValue(false)
            }
    }

    fun logOut(){
        firebaseAuth.signOut()
        isLoggedOutMutableLiveData.postValue(true)
    }

}
