package com.example.pantrysmart.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Destination : NavKey {
    @Serializable
    data object Search : Destination
    
    @Serializable
    data class Detail(val recipeId: Long) : Destination
    
    @Serializable
    data object Favorites : Destination

    @Serializable
    data object ShoppingList : Destination
}
