package com.example.skafferiet.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.skafferiet.MainActivity
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

        // Click "Favoriter" in the dropdown menu
        composeTestRule.onNodeWithText("Favoriter").performClick()

        // Assert that the favorites header is displayed
        composeTestRule.onNodeWithText("Favoriter").assertIsDisplayed()
    }

    @Test
    fun navigateToShoppingList_fromSearchScreen() {
        // Wait for the app to settle
        composeTestRule.waitForIdle()

        // Open the menu dropdown
        composeTestRule.onNodeWithContentDescription("Menu").performClick()

        // Click "Inköpslista" in the dropdown menu
        composeTestRule.onNodeWithText("Inköpslista").performClick()

        // Assert that the shopping list header is displayed
        composeTestRule.onNodeWithText("Inköpslista").assertIsDisplayed()
    }
}
