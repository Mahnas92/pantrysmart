package com.example.pantrysmart.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pantrysmart.domain.repository.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val recipeRepository: RecipeRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val recentSearches: StateFlow<List<String>> = recipeRepository.getRecentSearches()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<SearchUiState> = searchQuery
        .debounce(500.milliseconds)
        .distinctUntilChanged()
        .onEach { query ->
            if (query.isNotBlank()) {
                recipeRepository.addSearchToHistory(query)
            }
        }
        .flatMapLatest { query ->
            if (query.isBlank()) {
                recipeRepository.getAllRecipes()
                    .map { recipes ->
                        if (recipes.isEmpty()) {
                            SearchUiState.Empty
                        } else {
                            SearchUiState.Success(recipes)
                        }
                    }
                    .catch { emit(SearchUiState.Error(it.message ?: "Unknown error")) }
            } else {
                recipeRepository.getRecipes(query)
                    .map { recipes ->
                        if (recipes.isEmpty()) {
                            SearchUiState.Empty
                        } else {
                            SearchUiState.Success(recipes)
                        }
                    }
                    .catch { emit(SearchUiState.Error(it.message ?: "Unknown error")) }
                    .onStart { emit(SearchUiState.Loading) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchUiState.Empty
        )

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deleteHistoryItem(query: String) {
        viewModelScope.launch {
            recipeRepository.deleteSearchFromHistory(query)
        }
    }

    companion object {
        fun provideFactory(recipeRepository: RecipeRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(recipeRepository) as T
                }
            }
    }
}
