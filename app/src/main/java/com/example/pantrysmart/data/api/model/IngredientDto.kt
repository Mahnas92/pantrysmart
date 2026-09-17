package com.example.pantrysmart.data.api.model

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val id: Int? = null,
    val original: String,
    val amount: Double,
    val unit: String,
    val name: String,
    val image: String? = null
)
