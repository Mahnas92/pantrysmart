package com.example.skafferiet.di

import android.content.Context
import com.example.skafferiet.BuildConfig
import com.example.skafferiet.data.api.RetrofitClient
import com.example.skafferiet.data.local.SkafferiDatabase
import com.example.skafferiet.data.repository.OfflineFirstRecipeRepository
import com.example.skafferiet.domain.repository.RecipeRepository

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val recipeRepository: RecipeRepository
}

/**
 * [AppContainer] implementation that provides instance dependencies.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: SkafferiDatabase by lazy {
        SkafferiDatabase.getDatabase(context)
    }

    override val recipeRepository: RecipeRepository by lazy {
        OfflineFirstRecipeRepository(
            spoonacularService = RetrofitClient.spoonacularService,
            recipeDao = database.recipeDao(),
            apiKey = BuildConfig.SPOONACULAR_API_KEY
        )
    }
}
