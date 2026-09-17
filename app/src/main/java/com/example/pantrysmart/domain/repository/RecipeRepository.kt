package com.example.pantrysmart.domain.repository

import com.example.pantrysmart.domain.model.Ingredient
import com.example.pantrysmart.domain.model.Recipe
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
     * Fetches all recipes from the local cache.
     */
    fun getAllRecipes(): Flow<List<Recipe>>

    /**
     * Fetches recent search terms.
     */
    fun getRecentSearches(): Flow<List<String>>

    /**
     * Adds a search term to the history.
     */
    suspend fun addSearchToHistory(query: String)

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

    /**
     * Fetches the explicit shopping list.
     */
    fun getShoppingList(): Flow<List<Ingredient>>

    /**
     * Adds multiple ingredients to the shopping list.
     */
    suspend fun addIngredientsToList(ingredients: List<Ingredient>)

    /**
     * Adds a single ingredient to the shopping list.
     */
    suspend fun addIngredientToList(ingredient: Ingredient)

    /**
     * Deletes an ingredient from the shopping list by name.
     */
    suspend fun deleteIngredientFromList(name: String)
}
