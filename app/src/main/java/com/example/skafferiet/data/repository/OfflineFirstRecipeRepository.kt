package com.example.skafferiet.data.repository

import android.util.Log
import com.example.skafferiet.data.api.SpoonacularService
import com.example.skafferiet.data.local.dao.RecipeDao
import com.example.skafferiet.data.local.entity.RecipeEntity
import com.example.skafferiet.data.mapper.toDomain
import com.example.skafferiet.data.mapper.toEntity
import com.example.skafferiet.domain.model.Recipe
import com.example.skafferiet.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OfflineFirstRecipeRepository(
    private val spoonacularService: SpoonacularService,
    private val recipeDao: RecipeDao,
    private val apiKey: String
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
            val response = spoonacularService.searchRecipes(query, 20, apiKey)
            val entities = response.results.map { it.toDomain().toEntity() }
            upsertAll(entities)
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error fetching recipes for query: $query", e)
            // Error handling: fallback is already handled by the DB observer
        }

        // Wait for the DB observation to complete (which happens when the flow is cancelled)
        dbJob.join()
    }

    override fun getRecipeDetails(id: Long): Flow<Recipe?> = channelFlow {
        val dbJob = launch {
            recipeDao.getRecipeById(id)
                .map { it?.toDomain() }
                .collect { send(it) }
        }

        try {
            val dto = spoonacularService.getRecipeInformation(id.toInt(), apiKey)
            upsertAll(listOf(dto.toDomain().toEntity()))
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

    private suspend fun upsertAll(entities: List<RecipeEntity>) {
        for (entity in entities) {
            val existing = recipeDao.getRecipeByIdOnce(entity.id)
            if (existing != null) {
                // Merge data: preserve existing details if the new entity has less information
                val merged = entity.copy(
                    isFavorite = existing.isFavorite,
                    ingredients = if (entity.ingredients.isNotEmpty()) entity.ingredients else existing.ingredients,
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
}
