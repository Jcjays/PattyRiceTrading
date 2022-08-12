package com.patriciajavier.pattyricetrading

import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object{
        lateinit var appContext: MyApp
            private set
    }
}
