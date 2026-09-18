package com.example.pantrysmart.data.local.entity

import kotlinx.serialization.Serializable

@Serializable
data class IngredientEntity(
    val id: Int?,
    val name: String,
    val original: String,
    val amount: Double,
    val unit: String,
    val image: String?,
    val additionalInfo: String? = null
)
