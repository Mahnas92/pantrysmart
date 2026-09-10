package com.example.skafferiet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list_items")
data class ShoppingListItemEntity(
    @PrimaryKey val name: String,
    val amount: Double,
    val unit: String
)
