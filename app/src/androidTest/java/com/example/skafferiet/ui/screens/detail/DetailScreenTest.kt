package com.example.skafferiet.ui.screens.detail

import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.skafferiet.domain.model.Ingredient
import com.example.skafferiet.domain.model.Recipe
import com.example.skafferiet.domain.repository.RecipeRepository
import com.example.skafferiet.ui.theme.PantrySmartTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testRecipe = Recipe(
        id = 1L,
        title = "Test Recipe",
        image = null,
        readyInMinutes = 30,
        servings = 4,
        sourceUrl = null,
        ingredients = listOf(
            Ingredient(id = 1, name = "Tomato", original = "2 Tomatoes", amount = 2.0, unit = "pcs", image = null)
        ),
        summary = "A test summary",
        instructions = "Test instructions"
    )

    private class FakeRecipeRepository(val recipe: Recipe) : RecipeRepository {
        var addIngredientCalled = false
        var addAllIngredientsCalled = false
        
        override fun getRecipes(query: String): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getRecipeDetails(id: Long): Flow<Recipe?> = flowOf(recipe)
        override suspend fun toggleFavorite(recipe: Recipe) {}
        override fun isFavorite(id: Long): Flow<Boolean> = flowOf(false)
        override fun getFavorites(): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getShoppingList(): Flow<List<Ingredient>> = flowOf(emptyList())
        override suspend fun addIngredientsToList(ingredients: List<Ingredient>) {
            addAllIngredientsCalled = true
        }
        override suspend fun addIngredientToList(ingredient: Ingredient) {
            addIngredientCalled = true
        }
        override suspend fun deleteIngredientFromList(name: String) {}
    }

    @Test
    fun detailScreen_showsRecipeDetails() {
        val fakeRepository = FakeRecipeRepository(testRecipe)
        val viewModel = DetailViewModel(recipeId = 1L, repository = fakeRepository)

        composeTestRule.setContent {
            PantrySmartTheme {
                DetailScreen(
                    viewModel = viewModel,
                    onBackClick = {},
                    onNavigateToFavorites = {},
                    onNavigateToShoppingList = {}
                )
            }
        }

        // Check if title is displayed
        composeTestRule.onNodeWithText("Test Recipe").assertIsDisplayed()

        // Check if ingredients are displayed
        composeTestRule.onNodeWithText("• 2 Tomatoes").assertIsDisplayed()

        // Check if instructions are displayed
        composeTestRule.onNodeWithText("Test instructions").assertIsDisplayed()
    }

    @Test
    fun detailScreen_clickingAddIngredient_callsRepository() {
        val fakeRepository = FakeRecipeRepository(testRecipe)
        val viewModel = DetailViewModel(recipeId = 1L, repository = fakeRepository)

        composeTestRule.setContent {
            PantrySmartTheme {
                DetailScreen(
                    viewModel = viewModel,
                    onBackClick = {},
                    onNavigateToFavorites = {},
                    onNavigateToShoppingList = {}
                )
            }
        }

        // Click the individual add button
        composeTestRule.onNodeWithContentDescription("Add Tomato").performClick()

        // Verify repository call
        assert(fakeRepository.addIngredientCalled)
    }

    @Test
    fun detailScreen_clickingAddAllIngredients_callsRepository() {
        val fakeRepository = FakeRecipeRepository(testRecipe)
        val viewModel = DetailViewModel(recipeId = 1L, repository = fakeRepository)

        composeTestRule.setContent {
            PantrySmartTheme {
                DetailScreen(
                    viewModel = viewModel,
                    onBackClick = {},
                    onNavigateToFavorites = {},
                    onNavigateToShoppingList = {}
                )
            }
        }

        // Click the "Add All" button
        composeTestRule.onNodeWithText("Add All").performClick()

        // Verify repository call
        assert(fakeRepository.addAllIngredientsCalled)
    }
}
