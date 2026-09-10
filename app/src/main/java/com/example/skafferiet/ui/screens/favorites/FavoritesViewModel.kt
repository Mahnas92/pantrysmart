package com.example.skafferiet.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skafferiet.domain.model.Recipe
import com.example.skafferiet.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    val favorites: StateFlow<List<Recipe>> = repository.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    companion object {
        fun provideFactory(repository: RecipeRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FavoritesViewModel(repository) as T
                }
            }
    }
}
