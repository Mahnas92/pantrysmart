package com.example.skafferiet.ui.screens.shopping

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.skafferiet.domain.model.Ingredient
import com.example.skafferiet.domain.model.Recipe
import com.example.skafferiet.domain.repository.RecipeRepository
import com.example.skafferiet.ui.theme.SkafferietTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class ShoppingListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testIngredients = listOf(
        Ingredient(id = 1, name = "Tomat", original = "2st Tomater", amount = 2.0, unit = "st", image = null),
        Ingredient(id = 2, name = "Lök", original = "1st Lök", amount = 1.0, unit = "st", image = null)
    )

    private class FakeRecipeRepository(val ingredients: List<Ingredient>) : RecipeRepository {
        var deleteIngredientCalled = false
        var deletedIngredientName: String? = null
        
        override fun getRecipes(query: String): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getRecipeDetails(id: Long): Flow<Recipe?> = flowOf(null)
        override suspend fun toggleFavorite(recipe: Recipe) {}
        override fun isFavorite(id: Long): Flow<Boolean> = flowOf(false)
        override fun getFavorites(): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getShoppingList(): Flow<List<Ingredient>> = flowOf(ingredients)
        override suspend fun addIngredientsToList(ingredients: List<Ingredient>) {}
        override suspend fun addIngredientToList(ingredient: Ingredient) {}
        override suspend fun deleteIngredientFromList(name: String) {
            deleteIngredientCalled = true
            deletedIngredientName = name
        }
    }

    @Test
    fun shoppingListScreen_showsItems() {
        val fakeRepository = FakeRecipeRepository(testIngredients)
        val viewModel = ShoppingListViewModel(repository = fakeRepository)

        composeTestRule.setContent {
            SkafferietTheme {
                ShoppingListScreen(
                    viewModel = viewModel,
                    onBackClick = {},
                    onNavigateToFavorites = {}
                )
            }
        }

        // Check if items are displayed (ShoppingListViewModel capitalizes names)
        composeTestRule.onNodeWithText("Tomat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lök").assertIsDisplayed()
    }

    @Test
    fun shoppingListScreen_clickingDelete_callsRepository() {
        val fakeRepository = FakeRecipeRepository(testIngredients)
        val viewModel = ShoppingListViewModel(repository = fakeRepository)

        composeTestRule.setContent {
            SkafferietTheme {
                ShoppingListScreen(
                    viewModel = viewModel,
                    onBackClick = {},
                    onNavigateToFavorites = {}
                )
            }
        }

        // Click the trash button for "Tomat"
        // Content description is "Ta bort Tomat" in ShoppingListScreen.kt
        composeTestRule.onNodeWithContentDescription("Ta bort Tomat").performClick()

        // Verify repository call
        assert(fakeRepository.deleteIngredientCalled)
        assert(fakeRepository.deletedIngredientName?.lowercase() == "tomat")
    }
}
