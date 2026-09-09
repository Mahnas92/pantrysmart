package com.example.skafferiet.ui.screens.search

import com.example.skafferiet.domain.model.Recipe

sealed interface SearchUiState {
    object Loading : SearchUiState
    data class Success(val recipes: List<Recipe>) : SearchUiState
    object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}
