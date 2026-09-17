package com.example.pantrysmart.di

import android.content.Context
import com.example.pantrysmart.BuildConfig
import com.example.pantrysmart.data.api.RetrofitClient
import com.example.pantrysmart.data.local.PantryDatabase
import com.example.pantrysmart.data.repository.OfflineFirstRecipeRepository
import com.example.pantrysmart.domain.repository.RecipeRepository

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

    private val database: PantryDatabase by lazy {
        PantryDatabase.getDatabase(context)
    }

    override val recipeRepository: RecipeRepository by lazy {
        OfflineFirstRecipeRepository(
            spoonacularService = RetrofitClient.spoonacularService,
            recipeDao = database.recipeDao(),
            shoppingListDao = database.shoppingListDao(),
            searchHistoryDao = database.searchHistoryDao(),
            apiKey = BuildConfig.SPOONACULAR_API_KEY
        )
    }
}
