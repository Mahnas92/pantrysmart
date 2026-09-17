package com.example.pantrysmart

import android.app.Application
import com.example.pantrysmart.di.AppContainer
import com.example.pantrysmart.di.DefaultAppContainer

class PantrySmartApplication : Application() {
    /**
     * AppContainer instance used by the rest of the classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
