package com.example.skafferiet.ui.screens.shopping

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.skafferiet.R
import com.example.skafferiet.ui.components.PantryScaffold
import com.example.skafferiet.ui.components.PantryTopBar
import com.example.skafferiet.ui.theme.spacing
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    viewModel: ShoppingListViewModel,
    onBackClick: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    val shoppingList by viewModel.shoppingList.collectAsState()
    
    // Local state for checked items (since we don't have a database for shopping list yet)
    val checkedItems = remember { mutableStateMapOf<String, Boolean>() }

    PantryScaffold(
        topBar = {
            PantryTopBar(
                title = stringResource(R.string.shopping_list_title),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button_cd))
                    }
                },
                onNavigateToFavorites = onNavigateToFavorites
            )
        }
    ) { paddingValues ->
        if (shoppingList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_shopping_list_message),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(MaterialTheme.spacing.large),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = MaterialTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                contentPadding = PaddingValues(
                    top = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.large
                )
            ) {
                items(shoppingList) { ingredient ->
                    val isChecked = checkedItems[ingredient.name] ?: false
                    
                    Surface(
                        onClick = { checkedItems[ingredient.name] = !isChecked },
                        shape = MaterialTheme.shapes.medium,
                        color = if (isChecked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.medium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checkedItems[ingredient.name] = it }
                            )
                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ingredient.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textDecoration = if (isChecked) TextDecoration.LineThrough else null
                                )
                                Text(
                                    text = stringResource(
                                        R.string.amount_unit_format,
                                        formatAmount(ingredient.amount),
                                        ingredient.unit
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { viewModel.deleteIngredient(ingredient.name) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.remove_ingredient_cd, ingredient.name),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatAmount(amount: Double): String {
    return if (amount == amount.toLong().toDouble()) {
        amount.toLong().toString()
    } else {
        String.format(Locale.US, "%.2f", amount)
    }
}
