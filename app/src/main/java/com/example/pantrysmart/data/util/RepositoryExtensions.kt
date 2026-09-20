package com.example.pantrysmart.data.util

import com.example.pantrysmart.data.local.entity.RecipeEntity
import com.example.pantrysmart.util.Constants

fun RecipeEntity?.isFresh(): Boolean {
    if (this == null) return false
    val ttlMillis = Constants.RECIPE_TTL_DAYS * 24 * 60 * 60 * 1000
    return System.currentTimeMillis() - lastUpdated < ttlMillis
}

fun List<RecipeEntity>.isFresh(): Boolean {
    if (isEmpty()) return false
    return all { it.isFresh() }
}
