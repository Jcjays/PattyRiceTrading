package com.patriciajavier.pattyricetrading

import android.app.Application

class MyApp : Application() {



    override fun onCreate() {
        super.onCreate()
        appContext = this
        accessRights = false
        userId = ""
    }

    companion object{
        lateinit var appContext: MyApp
            private set

        var accessRights: Boolean = false
        var userId : String = ""
    }
}
