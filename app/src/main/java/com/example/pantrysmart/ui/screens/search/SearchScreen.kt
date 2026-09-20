package com.example.pantrysmart.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pantrysmart.R
import com.example.pantrysmart.domain.model.Ingredient
import com.example.pantrysmart.domain.model.Recipe
import com.example.pantrysmart.domain.repository.RecipeRepository
import com.example.pantrysmart.ui.components.*
import com.example.pantrysmart.ui.theme.PantrySmartTheme
import com.example.pantrysmart.ui.theme.spacing
import com.example.pantrysmart.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

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
            // 1. Top: Active Filter Panel
            Text(
                text = stringResource(R.string.active_filters),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = MaterialTheme.spacing.medium, bottom = MaterialTheme.spacing.small)
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Horizontal scrolling row for active filters
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedIngredients.isEmpty()) {
                        Text(
                            text = "No active filters",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = MaterialTheme.spacing.small)
                        )
                    } else {
                        selectedIngredients.forEach { ingredient ->
                            InputChip(
                                selected = true,
                                onClick = { },
                                label = { Text(ingredient) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.RemoveCircle,
                                        contentDescription = stringResource(R.string.remove_ingredient_cd, ingredient),
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable { viewModel.removeIngredient(ingredient) }
                                    )
                                },
                                colors = InputChipDefaults.inputChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                
                // Prominent Refresh/Sync button at the end of the filter row
                FilledTonalIconButton(
                    onClick = viewModel::refreshSearch,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.refresh_search)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            // 2. Middle: Input Zone ("Adding to the Pot")
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onQueryChange,
                placeholder = { Text("What\'s in your pantry?") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            viewModel.addIngredient(searchQuery)
                            viewModel.onQueryChange("")
                        }
                    }
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                viewModel.addIngredient(searchQuery)
                                viewModel.onQueryChange("")
                            }
                        },
                        enabled = searchQuery.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = "Add to Active Filters",
                            tint = if (searchQuery.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // 3. Bottom: The Pantry (Recent Ingredients) / Your Pantry History
            Text(
                text = "Your Pantry History",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
            )
            
            if (recentSearches.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                    maxLines = 3
                ) {
                    recentSearches.take(Constants.SEARCH_HISTORY_LIMIT).forEach { term ->
                        InputChip(
                            selected = false,
                            onClick = { viewModel.addIngredient(term) },
                            label = { Text(term) },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.delete_search_history),
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { viewModel.deleteHistoryItem(term) }
                                )
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = InputChipDefaults.inputChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            } else {
                Text(
                    text = "No history items yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = MaterialTheme.spacing.small)
                )
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            // Results Section
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

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun SearchScreenPreview() {
    val fakeRepository = object : RecipeRepository {
        override fun searchRecipes(query: String, ingredients: List<String>?): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getAllRecipes(): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getRecentSearches(): Flow<List<String>> = flowOf(listOf("Tomato", "Garlic", "Onion", "Cheese", "Pasta"))
        override suspend fun addSearchToHistory(query: String) {}
        override suspend fun deleteSearchFromHistory(query: String) {}
        override fun getRecipeDetails(id: Long): Flow<Recipe?> = flowOf(null)
        override suspend fun toggleFavorite(recipe: Recipe) {}
        override fun isFavorite(id: Long): Flow<Boolean> = flowOf(false)
        override fun getFavorites(): Flow<List<Recipe>> = flowOf(emptyList())
        override fun getShoppingList(): Flow<List<Ingredient>> = flowOf(emptyList())
        override suspend fun addIngredientsToList(ingredients: List<Ingredient>) {}
        override suspend fun addIngredientToList(ingredient: Ingredient) {}
        override suspend fun deleteIngredientFromList(name: String, additionalInfo: String?) {}
    }
    val viewModel = remember { SearchViewModel(fakeRepository) }
    PantrySmartTheme {
        SearchScreen(
            viewModel = viewModel,
            onRecipeClick = {},
            onNavigateToFavorites = {},
            onNavigateToShoppingList = {}
        )
    }
}
