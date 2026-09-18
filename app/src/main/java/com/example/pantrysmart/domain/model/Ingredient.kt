package com.example.pantrysmart.domain.model

data class Ingredient(
    val id: Int?,
    val name: String,
    val original: String,
    val amount: Double,
    val unit: String,
    val image: String?,
    val additionalInfo: String? = null
)
