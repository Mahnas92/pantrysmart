package com.example.pantrysmart.ui.screens.search

import com.example.pantrysmart.domain.model.Recipe
import com.example.pantrysmart.domain.repository.RecipeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.just
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private val repository: RecipeRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.getRecentSearches() } returns flowOf(emptyList())
        every { repository.getAllRecipes() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when query is empty, uiState should reflect all recipes`() = runTest {
        // Given
        val recipes = listOf(
            Recipe(id = 1, title = "Recipe 1", image = "", readyInMinutes = 30, servings = 4, sourceUrl = "", summary = "", instructions = "", ingredients = emptyList(), isFavorite = false)
        )
        every { repository.getAllRecipes() } returns flowOf(recipes)
        viewModel = SearchViewModel(repository)

        // When
        val job = launch { viewModel.uiState.collect {} }
        advanceTimeBy(1000) // Advance time to bypass debounce

        val state = viewModel.uiState.value

        // Then
        assertTrue("State should be Success but was $state", state is SearchUiState.Success)
        assertEquals(recipes, (state as SearchUiState.Success).recipes)
        job.cancel()
    }

    @Test
    fun `recentSearches flow collects from repository`() = runTest {
        // Given
        val history = listOf("Pasta", "Pizza")
        every { repository.getRecentSearches() } returns flowOf(history)
        viewModel = SearchViewModel(repository)

        // When
        val result = viewModel.recentSearches.first()

        // Then
        assertEquals(history, result)
    }

    @Test
    fun `when query is not blank, it is saved to history`() = runTest {
        // Given
        val query = "Pasta"
        coEvery { repository.addSearchToHistory(query) } just Runs
        every { repository.getRecipes(query) } returns flowOf(emptyList())
        viewModel = SearchViewModel(repository)

        // When
        viewModel.onQueryChange(query)
        advanceTimeBy(1500) // Debounce for history is 1000ms

        // Then
        coVerify { repository.addSearchToHistory(query) }
    }
}
