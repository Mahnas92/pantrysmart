package com.example.skafferiet.ui.screens.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.skafferiet.domain.model.Ingredient
import com.example.skafferiet.domain.model.Recipe
import com.example.skafferiet.ui.components.SkafferiScaffold
import com.example.skafferiet.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBackClick: () -> Unit
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
                onFavoriteToggle = { viewModel.toggleFavorite(state.recipe) }
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
    onFavoriteToggle: () -> Unit
) {
    SkafferiScaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe.title, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
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
                        IngredientsList(recipe.ingredients)
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
                    IngredientsList(recipe.ingredients)
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                    InstructionsSection(recipe.instructions)
                }
            }
        }
    }
}

@Composable
fun RecipeImage(imageUrl: String?, title: String) {
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
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun IngredientsList(ingredients: List<Ingredient>) {
    Text(
        text = "Ingredienser",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
    ingredients.forEach { ingredient ->
        Text(
            text = "• ${ingredient.original}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 2.dp)
        )
    }
}

@Composable
fun InstructionsSection(instructions: String?) {
    Text(
        text = "Instruktioner",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))
    Text(
        text = instructions ?: "Inga instruktioner tillgängliga.",
        style = MaterialTheme.typography.bodyMedium
    )
}
