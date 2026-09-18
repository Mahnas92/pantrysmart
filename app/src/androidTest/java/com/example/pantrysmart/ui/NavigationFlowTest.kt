package com.example.pantrysmart.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.pantrysmart.MainActivity
import com.example.pantrysmart.data.local.PantryDatabase
import com.example.pantrysmart.data.local.entity.RecipeEntity
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class NavigationFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigateToFavorites_fromSearchScreen() {
        // Wait for the app to settle
        composeTestRule.waitForIdle()

        // Open the menu dropdown
        composeTestRule.onNodeWithContentDescription("Menu").performClick()

        // Click "Favorites" in the dropdown menu
        composeTestRule.onNodeWithText("Favorites").performClick()

        // Assert that the favorites header is displayed
        composeTestRule.onNodeWithText("Favorites").assertIsDisplayed()
    }

    @Test
    fun navigateToShoppingList_fromSearchScreen() {
        // Wait for the app to settle
        composeTestRule.waitForIdle()

        // Open the menu dropdown
        composeTestRule.onNodeWithContentDescription("Menu").performClick()

        // Click "Shopping List" in the dropdown menu
        composeTestRule.onNodeWithText("Shopping List").performClick()

        // Assert that the shopping list header is displayed
        composeTestRule.onNodeWithText("Shopping List").assertIsDisplayed()
    }

    @Test
    fun navigateFromFavoritesToDetailAndBack_landsOnSearchScreen() {
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val database = PantryDatabase.getDatabase(context)
            
            database.recipeDao().deleteAll()
            val favoriteRecipe = RecipeEntity(
                id = 999L,
                title = "Favorite Test Recipe",
                image = null,
                readyInMinutes = 15,
                servings = 2,
                sourceUrl = null,
                summary = "A delicious favorite test recipe.",
                instructions = "Cook and enjoy.",
                ingredients = emptyList(),
                isFavorite = true,
            )
            database.recipeDao().insert(favoriteRecipe)

            composeTestRule.waitForIdle()

            // Open the menu dropdown
            composeTestRule.onNodeWithContentDescription("Menu").performClick()

            // Click "Favorites" in the dropdown menu
            composeTestRule.onNodeWithText("Favorites").performClick()

            // Click the favorite recipe to go to Detail screen
            composeTestRule.onNodeWithText("Favorite Test Recipe").performClick()
            composeTestRule.onNodeWithText("Favorite Test Recipe").assertIsDisplayed()

            // Press the back button on the Detail screen
            composeTestRule.onNodeWithContentDescription("Back").performClick()

            // Assert that we are back on the Search screen (app name PantrySmart is displayed)
            composeTestRule.onNodeWithText("PantrySmart").assertIsDisplayed()
        }
    }
}
