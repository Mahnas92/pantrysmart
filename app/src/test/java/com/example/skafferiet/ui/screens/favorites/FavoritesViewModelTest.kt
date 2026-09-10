package com.example.skafferiet.ui.screens.favorites

import com.example.skafferiet.domain.model.Recipe
import com.example.skafferiet.domain.repository.RecipeRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var viewModel: FavoritesViewModel
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
    fun `favorites flow collects recipes from repository`() = runTest {
        // Given
        val recipes = listOf(
            Recipe(id = 1, title = "Recipe 1", image = "", readyInMinutes = 30, servings = 4, sourceUrl = "", summary = "", instructions = "", ingredients = emptyList(), isFavorite = true),
            Recipe(id = 2, title = "Recipe 2", image = "", readyInMinutes = 20, servings = 2, sourceUrl = "", summary = "", instructions = "", ingredients = emptyList(), isFavorite = true)
        )
        every { repository.getFavorites() } returns flowOf(recipes)
        viewModel = FavoritesViewModel(repository)

        // When
        val result = viewModel.favorites.first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Recipe 1", result[0].title)
        assertEquals("Recipe 2", result[1].title)
    }
}
