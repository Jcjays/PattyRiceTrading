package com.patriciajavier.pattyricetrading

import android.app.Application
import timber.log.Timber

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        accessRights = false
        userId = ""

        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }

    }

    companion object{
        lateinit var appContext: MyApp
            private set

        var accessRights: Boolean = false
        var userId : String = ""
    }
}
