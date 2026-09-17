package com.example.pantrysmart.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.pantrysmart.MainActivity
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
}
