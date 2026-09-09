package com.example.skafferiet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "recipes")
@Serializable
data class RecipeEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val image: String?,
    val summary: String?,
    val ingredients: List<IngredientEntity>,
    val isFavorite: Boolean = false
)
