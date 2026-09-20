package com.example.pantrysmart.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pantrysmart.R
import com.example.pantrysmart.ui.components.*
import com.example.pantrysmart.ui.theme.spacing
import com.example.pantrysmart.util.Constants

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onRecipeClick: (Long) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToShoppingList: () -> Unit,
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedIngredients by viewModel.selectedIngredients.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    PantryScaffold(
        topBar = {
            PantryTopBar(
                title = stringResource(R.string.app_name),
                onNavigateToFavorites = onNavigateToFavorites,
                onNavigateToShoppingList = onNavigateToShoppingList
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = MaterialTheme.spacing.medium)
        ) {
            // 1. Active Filters (above input)
            if (selectedIngredients.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.active_filters),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium, bottom = MaterialTheme.spacing.small)
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
                ) {
                    selectedIngredients.forEach { ingredient ->
                        InputChip(
                            selected = true,
                            onClick = { },
                            label = { Text(ingredient) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = stringResource(R.string.remove_ingredient_cd, ingredient),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { viewModel.removeIngredient(ingredient) }
                                )
                            },
                            colors = InputChipDefaults.inputChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // 2. Input + Refresh Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onQueryChange,
                    placeholder = { Text(stringResource(R.string.search_recipes_placeholder)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChange("") }) {
                                Icon(Icons.Rounded.Clear, contentDescription = null)
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                Button(
                    onClick = viewModel::refreshSearch,
                    modifier = Modifier.height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Rounded.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(MaterialTheme.spacing.extraSmall))
                    Text(stringResource(R.string.refresh_search))
                }
            }

            // 3. Recent Ingredients (below input)
            if (recentSearches.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.recent_ingredients),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.medium, bottom = MaterialTheme.spacing.small)
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                    maxLines = 2
                ) {
                    recentSearches.take(Constants.SEARCH_HISTORY_LIMIT).forEach { term ->
                        InputChip(
                            selected = false,
                            onClick = { viewModel.addIngredient(term) },
                            label = { Text(term) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = stringResource(R.string.delete_search_history),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { viewModel.deleteHistoryItem(term) }
                                )
                            },
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Results
            Surface(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.background
            ) {
                when (val state = uiState) {
                    is SearchUiState.Loading -> LoadingScreen()
                    is SearchUiState.Success -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                            contentPadding = PaddingValues(bottom = MaterialTheme.spacing.large),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.recipes, key = { it.id }) { recipe ->
                                RecipeCard(
                                    recipe = recipe,
                                    onClick = { onRecipeClick(recipe.id) }
                                )
                            }
                        }
                    }
                    is SearchUiState.Empty -> EmptyState(message = stringResource(R.string.no_recipes_found))
                    is SearchUiState.Error -> ErrorScreen(message = state.message)
                }
            }
        }
    }
}
