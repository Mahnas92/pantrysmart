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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val recipeRepository: RecipeRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedIngredients = MutableStateFlow<List<String>>(emptyList())
    val selectedIngredients: StateFlow<List<String>> = _selectedIngredients.asStateFlow()

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    init {
        refreshTrigger.tryEmit(Unit)
    }

    val recentSearches: StateFlow<List<String>> = recipeRepository.getRecentSearches()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<SearchUiState> = refreshTrigger
        .onEach {
            val query = _searchQuery.value
            if (query.isNotBlank()) {
                recipeRepository.addSearchToHistory(query.trim())
            }
        }
        .flatMapLatest {
            val query = _searchQuery.value.trim()
            val ingredients = _selectedIngredients.value
            
            if (query.isBlank() && ingredients.isEmpty()) {
                recipeRepository.getAllRecipes()
                    .map { recipes ->
                        if (recipes.isEmpty()) SearchUiState.Empty else SearchUiState.Success(recipes)
                    }
                    .catch { emit(SearchUiState.Error(it.message ?: "Unknown error")) }
            } else {
                recipeRepository.searchRecipes(query, ingredients)
                    .map { recipes ->
                        if (recipes.isEmpty()) SearchUiState.Empty else SearchUiState.Success(recipes)
                    }
                    .onStart { emit(SearchUiState.Loading) }
                    .catch { emit(SearchUiState.Error(it.message ?: "Unknown error")) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchUiState.Empty
        )

    fun onQueryChange(newQuery: String) {
        if (newQuery.endsWith(",") || newQuery.endsWith("\n")) {
            val ingredient = newQuery.dropLast(1).trim()
            if (ingredient.isNotEmpty()) {
                addIngredient(ingredient)
            }
            _searchQuery.value = ""
        } else {
            _searchQuery.value = newQuery
        }
    }

    fun addIngredient(ingredient: String) {
        val trimmed = ingredient.trim()
        if (trimmed.isNotEmpty() && trimmed !in _selectedIngredients.value) {
            _selectedIngredients.value = _selectedIngredients.value + trimmed
            viewModelScope.launch {
                recipeRepository.addSearchToHistory(trimmed)
            }
        }
    }

    fun removeIngredient(ingredient: String) {
        _selectedIngredients.value = _selectedIngredients.value - ingredient
    }

    fun refreshSearch() {
        refreshTrigger.tryEmit(Unit)
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
