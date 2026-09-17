package com.example.pantrysmart.ui.screens.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pantrysmart.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AggregatedIngredient(
    val name: String,
    val amount: Double,
    val unit: String,
    val isChecked: Boolean = false
)

class ShoppingListViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    val shoppingList: StateFlow<List<AggregatedIngredient>> = repository.getShoppingList()
        .map { ingredients ->
            ingredients
                .groupBy { it.name.lowercase().trim() }
                .map { (name, ingredients) ->
                    AggregatedIngredient(
                        name = name.replaceFirstChar { it.uppercase() },
                        amount = ingredients.sumOf { it.amount },
                        unit = ingredients.firstOrNull()?.unit ?: ""
                    )
                }
                .sortedBy { it.name }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteIngredient(name: String) {
        viewModelScope.launch {
            repository.deleteIngredientFromList(name)
        }
    }

    companion object {
        fun provideFactory(repository: RecipeRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ShoppingListViewModel(repository) as T
                }
            }
    }
}
