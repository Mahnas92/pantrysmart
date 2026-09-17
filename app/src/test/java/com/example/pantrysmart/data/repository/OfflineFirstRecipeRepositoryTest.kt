package com.example.pantrysmart.data.repository

import android.util.Log
import com.example.pantrysmart.data.api.SpoonacularService
import com.example.pantrysmart.data.api.model.RecipeDto
import com.example.pantrysmart.data.api.model.SearchResponseDto
import com.example.pantrysmart.data.local.dao.RecipeDao
import com.example.pantrysmart.data.local.dao.ShoppingListDao
import com.example.pantrysmart.data.local.entity.RecipeEntity
import com.example.pantrysmart.data.local.entity.ShoppingListItemEntity
import com.example.pantrysmart.domain.model.Ingredient
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
    private val shoppingListDao: ShoppingListDao = mockk()
    private val apiKey = "test_api_key"

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        repository = OfflineFirstRecipeRepository(service, dao, shoppingListDao, apiKey)
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

    @Test
    fun `getShoppingList returns mapped ingredients from DAO`() = runTest {
        // Given
        val entity = ShoppingListItemEntity("Sugar", 2.0, "cups")
        every { shoppingListDao.getAllItems() } returns flowOf(listOf(entity))

        // When
        val result = repository.getShoppingList().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Sugar", result[0].name)
        assertEquals(2.0, result[0].amount, 0.001)
        assertEquals("cups", result[0].unit)
    }

    @Test
    fun `addIngredientToList updates amount if item already exists`() = runTest {
        // Given
        val ingredient = Ingredient(null, "Sugar", "2 cups Sugar", 2.0, "cups", null)
        val existing = ShoppingListItemEntity("Sugar", 1.0, "cups")
        coEvery { shoppingListDao.getItemByName("Sugar") } returns existing
        coEvery { shoppingListDao.insertItem(any()) } just Runs

        // When
        repository.addIngredientToList(ingredient)

        // Then
        coVerify { shoppingListDao.insertItem(match { it.name == "Sugar" && it.amount == 3.0 }) }
    }

    @Test
    fun `addIngredientToList inserts new item if it doesn't exist`() = runTest {
        // Given
        val ingredient = Ingredient(null, "Sugar", "2 cups Sugar", 2.0, "cups", null)
        coEvery { shoppingListDao.getItemByName("Sugar") } returns null
        coEvery { shoppingListDao.insertItem(any()) } just Runs

        // When
        repository.addIngredientToList(ingredient)

        // Then
        coVerify { shoppingListDao.insertItem(match { it.name == "Sugar" && it.amount == 2.0 }) }
    }

    @Test
    fun `deleteIngredientFromList calls DAO delete`() = runTest {
        // Given
        coEvery { shoppingListDao.deleteItemByName("Sugar") } just Runs

        // When
        repository.deleteIngredientFromList("Sugar")

        // Then
        coVerify { shoppingListDao.deleteItemByName("Sugar") }
    }
}
