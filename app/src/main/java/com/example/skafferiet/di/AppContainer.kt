package com.example.skafferiet.di

import android.content.Context

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    // Future dependencies like Retrofit services and Room databases will be defined here
}

/**
 * [AppContainer] implementation that provides instance dependencies.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {
    // Future implementations of dependencies
}
