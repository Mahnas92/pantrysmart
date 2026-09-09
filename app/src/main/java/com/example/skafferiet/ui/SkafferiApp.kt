package com.example.skafferiet.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.skafferiet.ui.navigation.Destination
import com.example.skafferiet.ui.theme.SkafferietTheme

@Composable
fun SkafferiApp() {
    val backStack = rememberNavBackStack(Destination.Search)

    SkafferietTheme {
        NavDisplay(
            backStack = backStack,
            entryProvider = { key ->
                when (key) {
                    is Destination.Search -> NavEntry(key) {
                        Text("Search Screen")
                    }
                    is Destination.Detail -> NavEntry(key) {
                        Text("Detail Screen for ID: ${key.recipeId}")
                    }
                    is Destination.Favorites -> NavEntry(key) {
                        Text("Favorites Screen")
                    }
                    else -> error("Unknown destination: $key")
                }
            }
        )
    }
}
