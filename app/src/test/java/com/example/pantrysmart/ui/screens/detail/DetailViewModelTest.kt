package com.example.pantrysmart.ui.screens.detail

import com.example.pantrysmart.domain.model.Recipe
import com.example.pantrysmart.domain.repository.RecipeRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private lateinit var viewModel: DetailViewModel
    private val repository: RecipeRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState reaches Success when recipe details are successfully fetched`() = runTest {
        // Given
        val recipeId = 1L
        val testRecipe = Recipe(
            id = recipeId,
            title = "Test Recipe",
            image = "",
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = "",
            summary = "",
            instructions = "",
            ingredients = emptyList(),
            isFavorite = false,
        )
        every { repository.getRecipeDetails(recipeId) } returns flowOf(testRecipe)
        every { repository.isFavorite(recipeId) } returns flowOf(value = false)
        every { repository.getShoppingList() } returns flowOf(emptyList())

        // When
        viewModel = DetailViewModel(recipeId, repository)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is DetailUiState.Success)
        val successState = state as DetailUiState.Success
        assertEquals("Test Recipe", successState.recipe.title)
    }
}
