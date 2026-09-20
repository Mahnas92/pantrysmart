package com.example.pantrysmart.ui.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pantrysmart.R
import com.example.pantrysmart.ui.components.*
import com.example.pantrysmart.ui.theme.spacing

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
            IngredientChipField(
                searchQuery = searchQuery,
                selectedIngredients = selectedIngredients,
                onQueryChange = viewModel::onQueryChange,
                onIngredientRemove = viewModel::removeIngredient,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.medium)
            )

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
                    maxLines = 3
                ) {
                    recentSearches.take(20).forEach { term ->
                        SuggestionChip(
                            onClick = { viewModel.addIngredient(term) },
                            label = { Text(term) },
                            shape = MaterialTheme.shapes.medium
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IngredientChipField(
    searchQuery: String,
    selectedIngredients: List<String>,
    onQueryChange: (String) -> Unit,
    onIngredientRemove: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.medium, vertical = MaterialTheme.spacing.extraSmall)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = MaterialTheme.spacing.small)
            )
            
            FlowRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
            ) {
                selectedIngredients.forEach { ingredient ->
                    InputChip(
                        selected = false,
                        onClick = { },
                        label = { Text(ingredient, style = MaterialTheme.typography.bodySmall) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onIngredientRemove(ingredient) }
                            )
                        },
                        shape = MaterialTheme.shapes.small,
                        colors = InputChipDefaults.inputChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
                
                Box(
                    modifier = Modifier
                        .widthIn(min = 80.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    if (searchQuery.isEmpty() && selectedIngredients.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_recipes_placeholder),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = MaterialTheme.spacing.small),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        singleLine = true
                    )
                }
            }
        }
    }
}
