package com.example.skafferiet.domain.repository

import com.example.skafferiet.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the operations for fetching and managing recipes.
 */
interface RecipeRepository {
    /**
     * Fetches recipes matching the given query.
     */
    fun getRecipes(query: String): Flow<List<Recipe>>

    /**
     * Fetches details for a specific recipe by its ID.
     */
    fun getRecipeDetails(id: Long): Flow<Recipe?>

    /**
     * Toggles the favorite status of a recipe.
     */
    suspend fun toggleFavorite(recipe: Recipe)

    /**
     * Checks if a recipe is marked as a favorite.
     */
    fun isFavorite(id: Long): Flow<Boolean>

    /**
     * Fetches all favorited recipes.
     */
    fun getFavorites(): Flow<List<Recipe>>
}
