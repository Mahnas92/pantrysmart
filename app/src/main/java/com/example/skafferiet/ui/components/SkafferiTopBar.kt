package com.example.skafferiet.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@ExperimentalMaterial3Api
@Composable
fun SkafferiTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavigateToFavorites: (() -> Unit)? = null,
    onNavigateToShoppingList: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = navigationIcon,
        actions = {
            actions()
            if (onNavigateToFavorites != null || onNavigateToShoppingList != null) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (onNavigateToFavorites != null) {
                        DropdownMenuItem(
                            text = { Text("Favoriter") },
                            onClick = {
                                showMenu = false
                                onNavigateToFavorites()
                            },
                            leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) }
                        )
                    }
                    if (onNavigateToShoppingList != null) {
                        DropdownMenuItem(
                            text = { Text("Inköpslista") },
                            onClick = {
                                showMenu = false
                                onNavigateToShoppingList()
                            },
                            leadingIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) }
                        )
                    }
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}
