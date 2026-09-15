package com.example.skafferiet.ui.screens.detail

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.skafferiet.R
import com.example.skafferiet.domain.model.Ingredient
import com.example.skafferiet.domain.model.Recipe
import com.example.skafferiet.ui.components.PantryScaffold
import com.example.skafferiet.ui.components.PantryTopBar
import com.example.skafferiet.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBackClick: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToShoppingList: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is DetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is DetailUiState.Success -> {
            RecipeDetailContent(
                recipe = state.recipe,
                isFavorite = state.isFavorite,
                onBackClick = onBackClick,
                onFavoriteToggle = { viewModel.toggleFavorite() },
                onAddIngredient = { viewModel.addIngredientToShoppingList(it) },
                onAddAllIngredients = { viewModel.addAllIngredientsToShoppingList(it) },
                onNavigateToFavorites = onNavigateToFavorites,
                onNavigateToShoppingList = onNavigateToShoppingList
            )
        }
        is DetailUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailContent(
    recipe: Recipe,
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onAddIngredient: (Ingredient) -> Unit,
    onAddAllIngredients: (List<Ingredient>) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToShoppingList: () -> Unit
) {
    PantryScaffold(
        topBar = {
            PantryTopBar(
                title = recipe.title,
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button_cd))
                    }
                },
                actions = {
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) {
                                stringResource(R.string.remove_from_favorites_cd)
                            } else {
                                stringResource(R.string.add_to_favorites_cd)
                            },
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                onNavigateToFavorites = onNavigateToFavorites,
                onNavigateToShoppingList = onNavigateToShoppingList
            )
        }
    ) { _ ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val isWideScreen = maxWidth > 600.dp
            
            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = MaterialTheme.spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.large)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        RecipeImage(recipe.image, recipe.title)
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                        IngredientsList(
                            ingredients = recipe.ingredients,
                            onAddIngredient = onAddIngredient
                        ) { onAddAllIngredients(recipe.ingredients) }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        InstructionsSection(recipe.instructions)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = MaterialTheme.spacing.medium)
                ) {
                    RecipeImage(recipe.image, recipe.title)
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                    IngredientsList(
                        ingredients = recipe.ingredients,
                        onAddIngredient = onAddIngredient
                    ) { onAddAllIngredients(recipe.ingredients) }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                    InstructionsSection(recipe.instructions)
                }
            }
        }
    }
}

@Composable
fun RecipeImage(imageUrl: String?, title: String) {
    Log.d("AsyncImage", "Loading image for $title: $imageUrl")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            error = rememberVectorPainter(Icons.Default.Warning)
        )
    }
}

@Composable
fun IngredientsList(
    ingredients: List<Ingredient>,
    onAddIngredient: (Ingredient) -> Unit,
    onAddAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.ingredients_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onAddAll) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
            Text(stringResource(R.string.add_all_ingredients))
        }
    }
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
    ingredients.forEach { ingredient ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.ingredient_bullet_format, ingredient.original),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp)
            )
            IconButton(
                onClick = { onAddIngredient(ingredient) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_ingredient_cd, ingredient.name),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun InstructionsSection(instructions: String?) {
    Text(
        text = stringResource(R.string.instructions_title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
    Text(
        text = instructions ?: stringResource(R.string.no_instructions_available),
        style = MaterialTheme.typography.bodyMedium
    )
}
