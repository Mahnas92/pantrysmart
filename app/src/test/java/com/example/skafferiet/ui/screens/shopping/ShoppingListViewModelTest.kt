package com.example.skafferiet.ui.screens.shopping

import com.example.skafferiet.domain.model.Ingredient
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
    fun `shoppingList displays ingredients from repository shopping list`() = runTest {
        // Given
        val ingredients = listOf(
            Ingredient(id = 1, name = "Tomato", original = "100g Tomato", amount = 100.0, unit = "g", image = ""),
            Ingredient(id = 2, name = "Onion", original = "1 Onion", amount = 1.0, unit = "piece", image = ""),
            Ingredient(id = 3, name = "Tomato", original = "200g Tomato", amount = 200.0, unit = "g", image = "")
        )

        every { repository.getShoppingList() } returns flowOf(ingredients)
        viewModel = ShoppingListViewModel(repository)

        // When
        val result = viewModel.shoppingList.first()

        // Then
        assertEquals(2, result.size)
        
        val tomato = result.find { it.name == "Tomato" }
        assertEquals(300.0, tomato?.amount ?: 0.0, 0.01)
        assertEquals("g", tomato?.unit)

        val onion = result.find { it.name == "Onion" }
        assertEquals(1.0, onion?.amount ?: 0.0, 0.01)
        assertEquals("piece", onion?.unit)
    }

    @Test
    fun `shoppingList is empty when repository shopping list is empty`() = runTest {
        // Given
        every { repository.getShoppingList() } returns flowOf(emptyList())
        viewModel = ShoppingListViewModel(repository)

        // When
        val result = viewModel.shoppingList.first()

        // Then
        assertEquals(0, result.size)
    }
}
