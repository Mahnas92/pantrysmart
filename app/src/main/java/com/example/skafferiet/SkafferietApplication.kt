package com.example.skafferiet

import android.app.Application
import com.example.skafferiet.di.AppContainer
import com.example.skafferiet.di.DefaultAppContainer

class SkafferietApplication : Application() {
    /**
     * AppContainer instance used by the rest of the classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
