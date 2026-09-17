package com.example.pantrysmart.ui.screens.search

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.pantrysmart.domain.model.Ingredient
import com.example.pantrysmart.domain.model.Recipe
import com.example.pantrysmart.domain.repository.RecipeRepository
import com.example.pantrysmart.ui.theme.PantrySmartTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val fakeRecipes = listOf(
        Recipe(
            id = 1L,
            title = "Test Recept",
            image = null,
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = null,
            ingredients = emptyList(),
            summary = "En test sammanfattning",
            instructions = "Test instruktioner"
        )
    )

    private val fakeRepository = object : RecipeRepository {
        override fun getRecipes(query: String): Flow<List<Recipe>> {
            return if (query == "Test") flowOf(fakeRecipes) else flowOf(emptyList())
        }
        override fun getRecipeDetails(id: Long): Flow<Recipe?> = flowOf(null)
        override suspend fun toggleFavorite(recipe: Recipe) {}
        override fun isFavorite(id: Long): Flow<Boolean> = flowOf(false)
        override fun getFavorites(): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getShoppingList(): Flow<List<Ingredient>> = flowOf(emptyList())
        override suspend fun addIngredientsToList(ingredients: List<Ingredient>) {}
        override suspend fun addIngredientToList(ingredient: Ingredient) {}
        override suspend fun deleteIngredientFromList(name: String) {}
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun searchScreen_showsResults_whenQueryEntered() {
        val viewModel = SearchViewModel(fakeRepository)

        composeTestRule.setContent {
            PantrySmartTheme {
                SearchScreen(
                    viewModel = viewModel,
                    onRecipeClick = {},
                    onNavigateToFavorites = {},
                    onNavigateToShoppingList = {}
                )
            }
        }

        // Wait for UI to be ready
        composeTestRule.waitForIdle()

        // Enter search query
        composeTestRule.onNodeWithText("Search recipes…").performTextInput("Test")

        // Wait for debounce and check if result is displayed
        composeTestRule.waitUntilAtLeastOneExists(
            matcher = hasText("Test Recipe"),
            timeoutMillis = 5000
        )

        composeTestRule.onNodeWithText("Test Recipe").assertIsDisplayed()
    }
}
