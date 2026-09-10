package com.example.skafferiet.ui.screens.shopping

import com.example.skafferiet.domain.model.Ingredient
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
class ShoppingListViewModelTest {

    private lateinit var viewModel: ShoppingListViewModel
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
    fun `shoppingList aggregates ingredients correctly across multiple favorited recipes`() = runTest {
        // Given
        val recipe1 = Recipe(
            id = 1,
            title = "Recipe 1",
            image = "",
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = "",
            summary = "",
            instructions = "",
            ingredients = listOf(
                Ingredient(id = 1, name = "Tomato", original = "100g Tomato", amount = 100.0, unit = "g", image = ""),
                Ingredient(id = 2, name = "Onion", original = "1 Onion", amount = 1.0, unit = "piece", image = "")
            ),
            isFavorite = true
        )
        val recipe2 = Recipe(
            id = 2,
            title = "Recipe 2",
            image = "",
            readyInMinutes = 20,
            servings = 2,
            sourceUrl = "",
            summary = "",
            instructions = "",
            ingredients = listOf(
                Ingredient(id = 1, name = "tomato ", original = "200g Tomato", amount = 200.0, unit = "g", image = ""),
                Ingredient(id = 3, name = "Garlic", original = "2 cloves Garlic", amount = 2.0, unit = "cloves", image = "")
            ),
            isFavorite = true
        )

        every { repository.getFavorites() } returns flowOf(listOf(recipe1, recipe2))
        viewModel = ShoppingListViewModel(repository)

        // When
        val result = viewModel.shoppingList.first()

        // Then
        assertEquals(3, result.size)
        
        val tomato = result.find { it.name == "Tomato" }
        assertEquals(300.0, tomato?.amount ?: 0.0, 0.01)
        assertEquals("g", tomato?.unit)

        val onion = result.find { it.name == "Onion" }
        assertEquals(1.0, onion?.amount ?: 0.0, 0.01)
        assertEquals("piece", onion?.unit)

        val garlic = result.find { it.name == "Garlic" }
        assertEquals(2.0, garlic?.amount ?: 0.0, 0.01)
        assertEquals("cloves", garlic?.unit)
    }

    @Test
    fun `shoppingList is empty when no favorites exist`() = runTest {
        // Given
        every { repository.getFavorites() } returns flowOf(emptyList())
        viewModel = ShoppingListViewModel(repository)

        // When
        val result = viewModel.shoppingList.first()

        // Then
        assertEquals(0, result.size)
    }
}
