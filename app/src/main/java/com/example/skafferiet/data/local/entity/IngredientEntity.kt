package com.example.skafferiet.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
data class IngredientEntity(
    val name: String,
    val amount: Double,
    val unit: String
)
