package com.patriciajavier.pattyricetrading.home.admin.inventory.order

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.patriciajavier.pattyricetrading.MyApp
import com.patriciajavier.pattyricetrading.R
import com.patriciajavier.pattyricetrading.databinding.FragmentOrderModuleScreenBinding

class OrderNotifications:AppCompatActivity() {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder


     fun onViewCreated(view: View, savedInstanceState: Bundle?) {

         if(MyApp.accessRights){
             //get notification

         }else{
             // send notification
         }
         }
}