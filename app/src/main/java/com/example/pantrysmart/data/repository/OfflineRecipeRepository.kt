package com.example.pantrysmart.data.repository

import android.util.Log
import com.example.pantrysmart.data.api.SpoonacularService
import com.example.pantrysmart.data.local.dao.RecipeDao
import com.example.pantrysmart.data.local.dao.SearchHistoryDao
import com.example.pantrysmart.data.local.dao.ShoppingListDao
import com.example.pantrysmart.data.local.entity.RecipeEntity
import com.example.pantrysmart.data.mapper.toDomain
import com.example.pantrysmart.data.mapper.toEntity
import com.example.pantrysmart.data.mapper.toShoppingListItemEntity
import com.example.pantrysmart.domain.model.Ingredient
import com.example.pantrysmart.domain.model.Recipe
import com.example.pantrysmart.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OfflineRecipeRepository(
    private val spoonacularService: SpoonacularService,
    private val recipeDao: RecipeDao,
    private val shoppingListDao: ShoppingListDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val apiKey: String,
) : RecipeRepository {

    override fun getRecipes(query: String): Flow<List<Recipe>> = channelFlow {
        // Observe the database and send updates to the channel
        val dbJob = launch {
            recipeDao.searchRecipes(query)
                .map { entities -> entities.map { it.toDomain() } }
                .collect { send(it) }
        }

        // Fetch from network and update database
        try {
            val localRecipes = recipeDao.searchRecipes(query).first()
            val isFresh = localRecipes.isNotEmpty() && localRecipes.all {
                System.currentTimeMillis() - it.lastUpdated < TTL_MILLIS
            }

            if (!isFresh) {
                val response = spoonacularService.searchRecipes(query, 20, apiKey)
                val entities = response.results.map { it.toDomain().toEntity().copy(lastUpdated = System.currentTimeMillis()) }
                upsertAll(entities)
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error fetching recipes for query: $query", e)
            // Error handling: fallback is already handled by the DB observer
        }

        // Wait for the DB observation to complete (which happens when the flow is cancelled)
        dbJob.join()
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getRecentSearches(): Flow<List<String>> {
        return searchHistoryDao.getRecentSearches()
            .map { entities -> entities.map { it.query } }
    }

    override suspend fun addSearchToHistory(query: String) {
        searchHistoryDao.insertAndTrim(query)
    }

    override suspend fun deleteSearchFromHistory(query: String) {
        searchHistoryDao.delete(query)
    }

    override fun getRecipeDetails(id: Long): Flow<Recipe?> = channelFlow {
        val dbJob = launch {
            recipeDao.getRecipeById(id)
                .map { it?.toDomain() }
                .collect { send(it) }
        }

        try {
            val localRecipe = recipeDao.getRecipeByIdOnce(id)
            val isFresh = localRecipe != null && localRecipe.ingredients.isNotEmpty() && (System.currentTimeMillis() - localRecipe.lastUpdated < TTL_MILLIS)

            if (!isFresh) {
                val dto = spoonacularService.getRecipeInformation(id.toInt(), apiKey)
                val entity = dto.toDomain().toEntity().copy(lastUpdated = System.currentTimeMillis())
                upsertAll(listOf(entity))
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error fetching recipe details for id: $id", e)
            // Fallback to cache
        }

        dbJob.join()
    }

    override suspend fun toggleFavorite(recipe: Recipe) {
        recipeDao.updateFavoriteStatus(recipe.id, !recipe.isFavorite)
    }

    override fun isFavorite(id: Long): Flow<Boolean> {
        return recipeDao.isFavorite(id)
    }

    override fun getFavorites(): Flow<List<Recipe>> {
        return recipeDao.getFavoriteRecipes()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getShoppingList(): Flow<List<Ingredient>> {
        return shoppingListDao.getAllItems()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun addIngredientsToList(ingredients: List<Ingredient>) {
        ingredients.forEach { addIngredientToList(it) }
    }

    override suspend fun addIngredientToList(ingredient: Ingredient) {
        val addInfo = ingredient.additionalInfo ?: ""
        val existing = shoppingListDao.getItemByNameAndAdditionalInfo(ingredient.name, addInfo)
        if (existing != null) {
            val updated = existing.copy(
                amount = existing.amount + ingredient.amount
            )
            shoppingListDao.insertItem(updated)
        } else {
            shoppingListDao.insertItem(ingredient.toShoppingListItemEntity())
        }
    }

    override suspend fun deleteIngredientFromList(name: String, additionalInfo: String?) {
        if (additionalInfo != null) {
            shoppingListDao.deleteItemByNameAndAdditionalInfo(name, additionalInfo)
        } else {
            shoppingListDao.deleteItemByName(name)
        }
    }

    private suspend fun upsertAll(entities: List<RecipeEntity>) {
        for (entity in entities) {
            val existing = recipeDao.getRecipeByIdOnce(entity.id)
            if (existing != null) {
                // Merge data: preserve existing details if the new entity has less information
                val merged = entity.copy(
                    isFavorite = existing.isFavorite,
                    ingredients = entity.ingredients.ifEmpty { existing.ingredients },
                    instructions = entity.instructions ?: existing.instructions,
                    summary = entity.summary ?: existing.summary,
                    readyInMinutes = entity.readyInMinutes ?: existing.readyInMinutes,
                    servings = entity.servings ?: existing.servings,
                    sourceUrl = entity.sourceUrl ?: existing.sourceUrl
                )
                recipeDao.insert(merged)
            } else {
                recipeDao.insert(entity)
            }
        }
    }

    companion object {
        const val TTL_MILLIS = 60L * 24 * 60 * 60 * 1000
    }
}
