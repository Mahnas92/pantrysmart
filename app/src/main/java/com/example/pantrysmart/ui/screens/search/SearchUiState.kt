package com.example.pantrysmart.ui.screens.search

import com.example.pantrysmart.domain.model.Recipe

sealed interface SearchUiState {
    object Loading : SearchUiState
    data class Success(val recipes: List<Recipe>) : SearchUiState
    object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}
