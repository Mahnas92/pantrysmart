package com.example.pantrysmart.data.local.entity

import androidx.room.Entity

@Entity(tableName = "shopping_list_items", primaryKeys = ["name", "additionalInfo"])
data class ShoppingListItemEntity(
    val name: String,
    val additionalInfo: String,
    val amount: Double,
    val unit: String
)
