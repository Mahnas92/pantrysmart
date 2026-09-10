package com.example.skafferiet.data.repository

import android.util.Log
import com.example.skafferiet.data.api.SpoonacularService
import com.example.skafferiet.data.api.model.RecipeDto
import com.example.skafferiet.data.api.model.SearchResponseDto
import com.example.skafferiet.data.local.dao.RecipeDao
import com.example.skafferiet.data.local.entity.RecipeEntity
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OfflineFirstRecipeRepositoryTest {

    private lateinit var repository: OfflineFirstRecipeRepository
    private val service: SpoonacularService = mockk()
    private val dao: RecipeDao = mockk()
    private val apiKey = "test_api_key"

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        repository = OfflineFirstRecipeRepository(service, dao, apiKey)
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `getRecipes fetches from service and saves to DAO on success`() = runTest {
        // Given
        val query = "pasta"
        val recipeDto = RecipeDto(id = 1, title = "Pasta")
        val searchResponse = SearchResponseDto(
            results = listOf(recipeDto),
            offset = 0,
            number = 1,
            totalResults = 1
        )
        
        coEvery { service.searchRecipes(query, 20, apiKey) } returns searchResponse
        coEvery { dao.getRecipeByIdOnce(1L) } returns null
        coEvery { dao.insert(any()) } just Runs
        every { dao.searchRecipes(query) } returns flowOf(emptyList())

        // When
        repository.getRecipes(query).first()

        // Then
        coVerify { service.searchRecipes(query, 20, apiKey) }
        coVerify { dao.insert(match { it.id == 1L && it.title == "Pasta" }) }
    }

    @Test
    fun `getRecipes returns data from DAO even if service throws exception`() = runTest {
        // Given
        val query = "pasta"
        val cachedRecipe = RecipeEntity(
            id = 1L,
            title = "Cached Pasta",
            image = null,
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = null,
            summary = null,
            instructions = null,
            ingredients = emptyList()
        )
        
        coEvery { service.searchRecipes(query, 20, apiKey) } throws Exception("Network error")
        every { dao.searchRecipes(query) } returns flowOf(listOf(cachedRecipe))

        // When
        val result = repository.getRecipes(query).first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Cached Pasta", result[0].title)
        coVerify { service.searchRecipes(query, 20, apiKey) }
    }

    @Test
    fun `getRecipes preserves isFavorite status when upserting existing recipe`() = runTest {
        // Given
        val query = "pasta"
        val recipeDto = RecipeDto(id = 1, title = "New Title")
        val searchResponse = SearchResponseDto(
            results = listOf(recipeDto),
            offset = 0,
            number = 1,
            totalResults = 1
        )
        val existingRecipe = RecipeEntity(
            id = 1L,
            title = "Old Title",
            image = null,
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = null,
            summary = null,
            instructions = null,
            ingredients = emptyList(),
            isFavorite = true
        )
        
        coEvery { service.searchRecipes(query, 20, apiKey) } returns searchResponse
        coEvery { dao.getRecipeByIdOnce(1L) } returns existingRecipe
        coEvery { dao.insert(any()) } just Runs
        every { dao.searchRecipes(query) } returns flowOf(listOf(existingRecipe))

        // When
        repository.getRecipes(query).first()

        // Then
        coVerify { dao.insert(match { it.id == 1L && it.title == "New Title" && it.isFavorite }) }
    }
}
