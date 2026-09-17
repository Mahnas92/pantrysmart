package com.example.pantrysmart.domain.model

data class Recipe(
    val id: Long,
    val title: String,
    val image: String?,
    val readyInMinutes: Int?,
    val servings: Int?,
    val sourceUrl: String?,
    val ingredients: List<Ingredient>,
    val summary: String?,
    val instructions: String?,
    val isFavorite: Boolean = false
)
