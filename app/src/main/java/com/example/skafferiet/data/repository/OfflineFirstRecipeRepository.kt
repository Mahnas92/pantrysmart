package com.example.skafferiet.data.repository

import com.example.skafferiet.data.api.SpoonacularService
import com.example.skafferiet.data.local.dao.RecipeDao
import com.example.skafferiet.data.local.entity.RecipeEntity
import com.example.skafferiet.data.mapper.toDomain
import com.example.skafferiet.data.mapper.toEntity
import com.example.skafferiet.domain.model.Recipe
import com.example.skafferiet.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class OfflineFirstRecipeRepository(
    private val spoonacularService: SpoonacularService,
    private val recipeDao: RecipeDao,
    private val apiKey: String
) : RecipeRepository {

    override fun getRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
            .map { entities -> entities.map { it.toDomain() } }
            .onStart {
                try {
                    val response = spoonacularService.searchRecipes(query, 20, apiKey)
                    val entities = response.results.map { it.toDomain().toEntity() }
                    upsertAll(entities)
                } catch (e: Exception) {
                    // Basic error handling as requested: ensures app doesn't crash
                    // and falls back to cached data from the database.
                }
            }
    }

    override fun getRecipeDetails(id: Long): Flow<Recipe?> {
        return recipeDao.getRecipeById(id)
            .map { it?.toDomain() }
            .onStart {
                try {
                    val dto = spoonacularService.getRecipeInformation(id.toInt(), apiKey)
                    upsertAll(listOf(dto.toDomain().toEntity()))
                } catch (e: Exception) {
                    // Fallback to cache
                }
            }
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
                recipeDao.insert(entity.copy(isFavorite = existing.isFavorite))
            } else {
                recipeDao.insert(entity)
            }
        }
    }
}
