package com.example.skafferiet.ui.screens.shopping

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.skafferiet.ui.components.SkafferiScaffold
import com.example.skafferiet.ui.components.SkafferiTopBar
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

    SkafferiScaffold(
        topBar = {
            SkafferiTopBar(
                title = "Inköpslista",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                    text = "Din inköpslista är tom. Lägg till recept i favoriter för att se ingredienser här.",
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
                                    text = "${formatAmount(ingredient.amount)} ${ingredient.unit}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
