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
    val additionalInfo: String? = null,
    val amount: Double,
    val unit: String,
    val isChecked: Boolean = false
)

class ShoppingListViewModel(
    private val repository: RecipeRepository,
) : ViewModel() {

    val shoppingList: StateFlow<List<AggregatedIngredient>> = repository.getShoppingList()
        .map { ingredients ->
            ingredients
                .asSequence()
                .groupBy { Pair(it.name.lowercase().trim(), it.additionalInfo?.lowercase()?.trim()) }
                .map { (key, list) ->
                    val firstIng = list.first()
                    AggregatedIngredient(
                        name = firstIng.name.replaceFirstChar { it.uppercase() },
                        additionalInfo = firstIng.additionalInfo,
                        amount = list.sumOf { it.amount },
                        unit = firstIng.unit
                    )
                }
                .sortedBy { it.name }
                .toList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteIngredient(name: String, additionalInfo: String? = null) {
        viewModelScope.launch {
            repository.deleteIngredientFromList(name, additionalInfo)
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
