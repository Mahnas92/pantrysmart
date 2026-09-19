package com.example.pantrysmart

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.example.pantrysmart.di.AppContainer
import com.example.pantrysmart.di.DefaultAppContainer

class PantrySmartApplication : Application(), SingletonImageLoader.Factory {
    /**
     * AppContainer instance used by the rest of the classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .allowHardware(false) // Better compatibility for some emulators
            .logger(DebugLogger())
            .build()
    }
}
