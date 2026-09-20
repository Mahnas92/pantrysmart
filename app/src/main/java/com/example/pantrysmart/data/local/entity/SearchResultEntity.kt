package com.example.pantrysmart.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_results")
data class SearchResultEntity(
    @PrimaryKey
    val searchKey: String,
    val recipeIds: List<Long>,
    val lastUpdated: Long
)
