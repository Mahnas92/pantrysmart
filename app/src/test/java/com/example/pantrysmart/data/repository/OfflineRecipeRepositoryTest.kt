package com.example.pantrysmart.data.repository

import android.util.Log
import com.example.pantrysmart.data.api.SpoonacularService
import com.example.pantrysmart.data.api.model.RecipeDto
import com.example.pantrysmart.data.api.model.SearchResponseDto
import com.example.pantrysmart.data.local.dao.RecipeDao
import com.example.pantrysmart.data.local.dao.ShoppingListDao
import com.example.pantrysmart.data.local.dao.SearchHistoryDao
import com.example.pantrysmart.data.local.dao.SearchResultDao
import com.example.pantrysmart.data.local.entity.IngredientEntity
import com.example.pantrysmart.data.local.entity.RecipeEntity
import com.example.pantrysmart.data.local.entity.SearchResultEntity
import com.example.pantrysmart.data.local.entity.ShoppingListItemEntity
import com.example.pantrysmart.util.Constants
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
class OfflineRecipeRepositoryTest {

    private lateinit var repository: OfflineRecipeRepository
    private val service: SpoonacularService = mockk()
    private val dao: RecipeDao = mockk()
    private val shoppingListDao: ShoppingListDao = mockk()
    private val searchHistoryDao: SearchHistoryDao = mockk()
    private val searchResultDao: SearchResultDao = mockk()
    private val apiKey = "test_api_key"

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        coEvery { searchHistoryDao.insertAndTrim(any()) } just Runs
        coEvery { searchResultDao.getSearchResult(any()) } returns null
        coEvery { searchResultDao.insertSearchResult(any()) } just Runs
        every { dao.getRecipesByIds(any()) } returns flowOf(emptyList())
        repository = OfflineRecipeRepository(service, dao, shoppingListDao, searchHistoryDao, searchResultDao, apiKey)
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
            totalResults = 1,
        )
        
        coEvery { service.searchRecipes(query, Constants.MAX_SEARCH_RESULTS, apiKey, any()) } returns searchResponse
        coEvery { dao.getRecipeByIdOnce(1L) } returns null
        coEvery { dao.insert(any()) } just Runs
        every { dao.searchRecipes(query) } returns flowOf(emptyList())
        every { dao.getRecipesByIds(any()) } returns flowOf(emptyList())

        // When
        repository.searchRecipes(query).first()

        // Then
        coVerify { service.searchRecipes(query, Constants.MAX_SEARCH_RESULTS, apiKey, any()) }
        coVerify { dao.insert(match { it.id == 1L && it.title == "Pasta" }) }
        coVerify { searchResultDao.insertSearchResult(any()) }
    }

    @Test
    fun `searchRecipes uses same searchKey for ingredients in different order`() = runTest {
        // Given
        val ingredients1 = listOf("salmon", "rice")
        val ingredients2 = listOf("rice", "salmon")
        val recipeDto = RecipeDto(id = 1, title = "Salmon Rice")
        val searchResponse = SearchResponseDto(
            results = listOf(recipeDto),
            offset = 0,
            number = 1,
            totalResults = 1,
        )

        coEvery { service.searchRecipes("", Constants.MAX_SEARCH_RESULTS, apiKey, "salmon,rice") } returns searchResponse
        coEvery { dao.getRecipeByIdOnce(1L) } returns null
        coEvery { dao.insert(any()) } just Runs
        every { dao.searchRecipes("") } returns flowOf(emptyList())
        every { dao.getRecipesByIds(any()) } returns flowOf(emptyList())

        // When
        repository.searchRecipes("", ingredients1).first()
        
        // Mock fresh cache for second call
        // Key is "rice,salmon" because of sorting
        val searchKey = "rice,salmon"
        val freshResult = SearchResultEntity(searchKey, listOf(1L), System.currentTimeMillis())
        coEvery { searchResultDao.getSearchResult(searchKey) } returns freshResult
        
        val recipeEntity = RecipeEntity(1L, "Salmon Rice", null, 30, 2, null, null, null, emptyList())
        every { dao.getRecipesByIds(listOf(1L)) } returns flowOf(listOf(recipeEntity))

        repository.searchRecipes("", ingredients2).first()

        // Then
        // Should only call service ONCE because the second call hits the cache
        coVerify(exactly = 1) { service.searchRecipes(any(), any(), any(), any()) }
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
            ingredients = emptyList(),
        )
        
        coEvery { service.searchRecipes(query, Constants.MAX_SEARCH_RESULTS, apiKey, any()) } throws Exception("Network error")
        every { dao.searchRecipes(query) } returns flowOf(listOf(cachedRecipe))
        every { dao.getRecipesByIds(any()) } returns flowOf(listOf(cachedRecipe))

        // When
        val result = repository.searchRecipes(query).first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Cached Pasta", result[0].title)
        coVerify { service.searchRecipes(query, Constants.MAX_SEARCH_RESULTS, apiKey, any()) }
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
            totalResults = 1,
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
            isFavorite = true,
        )
        
        coEvery { service.searchRecipes(query, Constants.MAX_SEARCH_RESULTS, apiKey, any()) } returns searchResponse
        coEvery { dao.getRecipeByIdOnce(1L) } returns existingRecipe
        coEvery { dao.insert(any()) } just Runs
        every { dao.searchRecipes(query) } returns flowOf(listOf(existingRecipe))
        
        // Mock the final emission
        val updatedRecipe = existingRecipe.copy(title = "New Title")
        every { dao.getRecipesByIds(listOf(1L)) } returns flowOf(listOf(updatedRecipe))

        // When
        repository.searchRecipes(query).first()

        // Then
        coVerify { dao.insert(match { it.id == 1L && it.title == "New Title" && it.isFavorite }) }
    }

    @Test
    fun `getRecipes skips network call if data is within TTL`() = runTest {
        // Given
        val query = "pasta"
        val freshRecipe = RecipeEntity(
            id = 1L,
            title = "Fresh Pasta",
            image = null,
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = null,
            summary = null,
            instructions = null,
            ingredients = emptyList(),
            lastUpdated = System.currentTimeMillis()
        )
        val searchKey = "query:pasta;ingredients:"
        val freshResult = SearchResultEntity(searchKey, listOf(1L), System.currentTimeMillis())
        coEvery { searchResultDao.getSearchResult(searchKey) } returns freshResult
        every { dao.getRecipesByIds(listOf(1L)) } returns flowOf(listOf(freshRecipe))

        // When
        repository.searchRecipes(query).first()

        // Then
        coVerify(exactly = 0) { service.searchRecipes(any(), any(), any(), any()) }
    }

    @Test
    fun `getRecipeDetails skips network call if data is within TTL`() = runTest {
        // Given
        val id = 1L
        val freshRecipe = RecipeEntity(
            id = id,
            title = "Fresh Pasta Details",
            image = null,
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = null,
            summary = null,
            instructions = null,
            ingredients = listOf(IngredientEntity(1, "Pasta", "Pasta", 1.0, "pkg", null, null)),
            lastUpdated = System.currentTimeMillis(),
        )
        every { dao.getRecipeById(id) } returns flowOf(freshRecipe)
        coEvery { dao.getRecipeByIdOnce(id) } returns freshRecipe

        // When
        repository.getRecipeDetails(id).first()

        // Then
        coVerify(exactly = 0) { service.getRecipeInformation(any(), any()) }
    }

    @Test
    fun `getRecipes performs network call if data is outside TTL`() = runTest {
        // Given
        val query = "pasta"
        val staleTime = System.currentTimeMillis() - (Constants.RECIPE_TTL_DAYS * 24 * 60 * 60 * 1000 + 1000)
        val staleRecipe = RecipeEntity(
            id = 1L,
            title = "Stale Pasta",
            image = null,
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = null,
            summary = null,
            instructions = null,
            ingredients = emptyList(),
            lastUpdated = staleTime
        )
        val searchKey = "query:pasta;ingredients:"
        val staleResult = SearchResultEntity(searchKey, listOf(1L), staleTime)
        coEvery { searchResultDao.getSearchResult(searchKey) } returns staleResult
        every { dao.getRecipesByIds(listOf(1L)) } returns flowOf(listOf(staleRecipe))

        val recipeDto = RecipeDto(id = 1, title = "Fresh Pasta From Net")
        val searchResponse = SearchResponseDto(
            results = listOf(recipeDto),
            offset = 0,
            number = 1,
            totalResults = 1,
        )
        coEvery { service.searchRecipes(query, Constants.MAX_SEARCH_RESULTS, apiKey, any()) } returns searchResponse
        coEvery { dao.getRecipeByIdOnce(1L) } returns staleRecipe
        coEvery { dao.insert(any()) } just Runs

        // When
        repository.searchRecipes(query).first()

        // Then
        coVerify(exactly = 1) { service.searchRecipes(query, Constants.MAX_SEARCH_RESULTS, apiKey, any()) }
    }

    @Test
    fun `getShoppingList returns mapped ingredients from DAO`() = runTest {
        // Given
        val entity = ShoppingListItemEntity("Sugar", "", 2.0, "cups")
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
        val existing = ShoppingListItemEntity("Sugar", "", 1.0, "cups")
        coEvery { shoppingListDao.getItemByNameAndAdditionalInfo("Sugar", "") } returns existing
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
        coEvery { shoppingListDao.getItemByNameAndAdditionalInfo("Sugar", "") } returns null
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
