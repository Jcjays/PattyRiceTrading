package com.patriciajavier.pattyricetrading

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseUser
import com.patriciajavier.pattyricetrading.firestore.models.User
import com.patriciajavier.pattyricetrading.registration.arch.LoggedInViewModel
import com.patriciajavier.pattyricetrading.registration.arch.SharedViewModel
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val viewModel : SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        MyApp.userId = viewModel.userMutableLiveData.value?.data?.uid.toString()

        Log.d("userId", MyApp.userId)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }
}