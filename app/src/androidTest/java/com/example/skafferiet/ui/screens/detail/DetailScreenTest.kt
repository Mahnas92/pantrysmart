package com.example.skafferiet.ui.screens.detail

import androidx.compose.material3.Text
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

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testRecipe = Recipe(
        id = 1L,
        title = "Test Recept",
        image = null,
        readyInMinutes = 30,
        servings = 4,
        sourceUrl = null,
        ingredients = listOf(
            Ingredient(id = 1, name = "Tomat", original = "2st Tomater", amount = 2.0, unit = "st", image = null)
        ),
        summary = "En test sammanfattning",
        instructions = "Test instruktioner"
    )

    private val fakeRepository = object : RecipeRepository {
        override fun getRecipes(query: String): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getRecipeDetails(id: Long): Flow<Recipe?> = flowOf(testRecipe)
        override suspend fun toggleFavorite(recipe: Recipe) {}
        override fun isFavorite(id: Long): Flow<Boolean> = flowOf(false)
        override fun getFavorites(): Flow<List<Recipe>> = flowOf(emptyList())
    }

    @Test
    fun detailScreen_showsRecipeDetails() {
        val viewModel = DetailViewModel(recipeId = 1L, repository = fakeRepository)

        composeTestRule.setContent {
            SkafferietTheme {
                DetailScreen(
                    viewModel = viewModel,
                    onBackClick = {},
                    onNavigateToFavorites = {},
                    onNavigateToShoppingList = {}
                )
            }
        }

        // Check if title is displayed
        composeTestRule.onNodeWithText("Test Recept").assertIsDisplayed()

        // Check if ingredients are displayed
        composeTestRule.onNodeWithText("• 2st Tomater").assertIsDisplayed()

        // Check if instructions are displayed
        composeTestRule.onNodeWithText("Test instruktioner").assertIsDisplayed()
    }
}
