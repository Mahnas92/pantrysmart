package com.example.skafferiet.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.skafferiet.ui.theme.SkafferietTheme
import org.junit.Rule
import org.junit.Test

class SkafferiScaffoldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun skafferiScaffold_displaysTitleAndContent() {
        val title = "Test Title"
        val content = "Test Content"

        composeTestRule.setContent {
            SkafferietTheme {
                SkafferiScaffold(
                    topBar = {
                        SkafferiTopBar(title = title)
                    }
                ) {
                    Text(text = content)
                }
            }
        }

        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(content).assertIsDisplayed()
    }
}
