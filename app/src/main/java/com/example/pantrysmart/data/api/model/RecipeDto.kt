package com.example.pantrysmart.data.api.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(
    val id: Int,
    val title: String,
    val image: String? = null,
    val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val sourceUrl: String? = null,
    val extendedIngredients: List<IngredientDto>? = null,
    val summary: String? = null,
    val instructions: String? = null
)
