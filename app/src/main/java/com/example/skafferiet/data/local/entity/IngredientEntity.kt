package com.example.skafferiet.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
data class IngredientEntity(
    val id: Int?,
    val name: String,
    val original: String,
    val amount: Double,
    val unit: String,
    val image: String?
)
