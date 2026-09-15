package com.example.skafferiet.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skafferiet.domain.model.Ingredient
import com.example.skafferiet.domain.model.Recipe
import com.example.skafferiet.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val recipe: Recipe, val isFavorite: Boolean) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    private val recipeId: Long,
    private val repository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    val isFavorite: StateFlow<Boolean> = repository.isFavorite(recipeId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        fetchRecipeDetails()
    }

    private fun fetchRecipeDetails() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            combine(
                repository.getRecipeDetails(recipeId),
                repository.isFavorite(recipeId)
            ) { recipe, isFavorite ->
                if (recipe != null) {
                    DetailUiState.Success(recipe, isFavorite)
                } else {
                    DetailUiState.Error("Recipe not found.")
                }
            }.catch { e ->
                _uiState.value = DetailUiState.Error(e.message ?: "An unexpected error occurred.")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleFavorite() {
        val state = _uiState.value
        if (state is DetailUiState.Success) {
            viewModelScope.launch {
                repository.toggleFavorite(state.recipe)
            }
        }
    }

    fun addIngredientToShoppingList(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.addIngredientToList(ingredient)
        }
    }

    fun addAllIngredientsToShoppingList(ingredients: List<Ingredient>) {
        viewModelScope.launch {
            repository.addIngredientsToList(ingredients)
        }
    }

    companion object {
        fun provideFactory(recipeId: Long, repository: RecipeRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetailViewModel(recipeId, repository) as T
                }
            }
    }
}
