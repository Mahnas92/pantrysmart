package com.example.pantrysmart.data.api.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchResponseDto(
    val results: List<RecipeDto>,
    val offset: Int,
    val number: Int,
    val totalResults: Int
)
