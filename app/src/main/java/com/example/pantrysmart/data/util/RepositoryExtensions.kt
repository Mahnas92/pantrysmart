package com.example.pantrysmart.data.util

import com.example.pantrysmart.data.local.entity.RecipeEntity

object CacheConfig {
    const val TTL_MILLIS = 60L * 24 * 60 * 60 * 1000 // 60 days
}

fun RecipeEntity?.isFresh(): Boolean {
    if (this == null) return false
    return System.currentTimeMillis() - lastUpdated < CacheConfig.TTL_MILLIS
}

fun List<RecipeEntity>.isFresh(): Boolean {
    if (isEmpty()) return false
    return all { it.isFresh() }
}
