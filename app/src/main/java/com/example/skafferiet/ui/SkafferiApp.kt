package com.example.skafferiet.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.skafferiet.SkafferietApplication
import com.example.skafferiet.ui.navigation.Destination
import com.example.skafferiet.ui.screens.search.SearchScreen
import com.example.skafferiet.ui.screens.search.SearchViewModel
import com.example.skafferiet.ui.theme.SkafferietTheme

@Composable
fun SkafferiApp() {
    val backStack = rememberNavBackStack(Destination.Search)
    val context = LocalContext.current
    val appContainer = (context.applicationContext as SkafferietApplication).container

    SkafferietTheme {
        NavDisplay(
            backStack = backStack,
            entryProvider = { key ->
                when (key) {
                    is Destination.Search -> NavEntry(key) {
                        val viewModel: SearchViewModel = viewModel(
                            factory = SearchViewModel.provideFactory(appContainer.recipeRepository)
                        )
                        SearchScreen(
                            viewModel = viewModel,
                            onRecipeClick = { recipeId ->
                                backStack.add(Destination.Detail(recipeId))
                            }
                        )
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
