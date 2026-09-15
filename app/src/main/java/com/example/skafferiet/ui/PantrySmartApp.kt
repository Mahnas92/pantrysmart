package com.example.skafferiet.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.skafferiet.PantrySmartApplication
import com.example.skafferiet.ui.navigation.Destination
import com.example.skafferiet.ui.screens.detail.DetailScreen
import com.example.skafferiet.ui.screens.detail.DetailViewModel
import com.example.skafferiet.ui.screens.favorites.FavoritesScreen
import com.example.skafferiet.ui.screens.favorites.FavoritesViewModel
import com.example.skafferiet.ui.screens.shopping.ShoppingListScreen
import com.example.skafferiet.ui.screens.shopping.ShoppingListViewModel
import com.example.skafferiet.ui.screens.search.SearchScreen
import com.example.skafferiet.ui.screens.search.SearchViewModel
import com.example.skafferiet.ui.theme.PantrySmartTheme

@Composable
fun PantrySmartApp() {
    val backStack = rememberNavBackStack(Destination.Search)
    val context = LocalContext.current
    val appContainer = (context.applicationContext as PantrySmartApplication).container

    PantrySmartTheme {
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
                            },
                            onNavigateToFavorites = { backStack.add(Destination.Favorites) },
                            onNavigateToShoppingList = { backStack.add(Destination.ShoppingList) }
                        )
                    }
                    is Destination.Detail -> NavEntry(key) {
                        val viewModel: DetailViewModel = viewModel(
                            key = key.recipeId.toString(),
                            factory = DetailViewModel.provideFactory(
                                recipeId = key.recipeId,
                                repository = appContainer.recipeRepository
                            )
                        )
                        DetailScreen(
                            viewModel = viewModel,
                            onBackClick = { backStack.remove(key) },
                            onNavigateToFavorites = { backStack.add(Destination.Favorites) },
                            onNavigateToShoppingList = { backStack.add(Destination.ShoppingList) }
                        )
                    }
                    is Destination.Favorites -> NavEntry(key) {
                        val viewModel: FavoritesViewModel = viewModel(
                            factory = FavoritesViewModel.provideFactory(appContainer.recipeRepository)
                        )
                        FavoritesScreen(
                            viewModel = viewModel,
                            onRecipeClick = { recipeId ->
                                backStack.add(Destination.Detail(recipeId))
                            },
                            onBackClick = { backStack.remove(key) },
                            onNavigateToShoppingList = { backStack.add(Destination.ShoppingList) }
                        )
                    }
                    is Destination.ShoppingList -> NavEntry(key) {
                        val viewModel: ShoppingListViewModel = viewModel(
                            factory = ShoppingListViewModel.provideFactory(appContainer.recipeRepository)
                        )
                        ShoppingListScreen(
                            viewModel = viewModel,
                            onBackClick = { backStack.remove(key) },
                            onNavigateToFavorites = { backStack.add(Destination.Favorites) }
                        )
                    }
                    else -> error("Unknown destination: $key")
                }
            }
        )
    }
}
