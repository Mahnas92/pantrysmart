package com.example.pantrysmart.data.mapper

import com.example.pantrysmart.data.local.entity.SearchHistoryEntity

// Placeholder for SearchHistory specific mappers if needed in the future
// Currently, SearchHistoryEntity and domain String are converted directly in Repository
// but we can add them here for consistency

fun String.toSearchHistoryEntity(): SearchHistoryEntity {
    return SearchHistoryEntity(
        query = this,
        timestamp = System.currentTimeMillis()
    )
}
