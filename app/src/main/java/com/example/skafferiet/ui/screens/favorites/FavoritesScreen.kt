package com.example.skafferiet.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.skafferiet.ui.components.SkafferiScaffold
import com.example.skafferiet.ui.components.SkafferiTopBar
import com.example.skafferiet.ui.screens.search.RecipeCard
import com.example.skafferiet.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onRecipeClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToShoppingList: () -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()

    SkafferiScaffold(
        topBar = {
            SkafferiTopBar(
                title = "Favoriter",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                onNavigateToShoppingList = onNavigateToShoppingList
            )
        }
    ) { paddingValues ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Du har inga sparade favoriter än.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                contentPadding = PaddingValues(
                    top = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.large
                )
            ) {
                items(favorites, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) }
                    )
                }
            }
        }
    }
}
