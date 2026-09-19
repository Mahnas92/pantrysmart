package com.example.pantrysmart.data.mapper

import com.example.pantrysmart.data.api.model.RecipeDto
import com.example.pantrysmart.data.local.entity.RecipeEntity
import com.example.pantrysmart.domain.model.Recipe

fun RecipeDto.toDomain(): Recipe {
    val fullImageUrl = if (image != null) {
        if (!image.startsWith("http")) {
            "https://img.spoonacular.com/recipes/$image"
        } else {
            image.replace("http://", "https://")
        }
    } else {
        null
    }
    return Recipe(
        id = id.toLong(),
        title = title,
        image = fullImageUrl,
        readyInMinutes = readyInMinutes,
        servings = servings,
        sourceUrl = sourceUrl,
        ingredients = extendedIngredients?.map { it.toDomain() } ?: emptyList(),
        summary = summary,
        instructions = instructions
    )
}

fun RecipeEntity.toDomain(): Recipe {
    return Recipe(
        id = id,
        title = title,
        image = image,
        readyInMinutes = readyInMinutes,
        servings = servings,
        sourceUrl = sourceUrl,
        ingredients = ingredients.map { it.toDomain() },
        summary = summary,
        instructions = instructions,
        isFavorite = isFavorite
    )
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        title = title,
        image = image,
        readyInMinutes = readyInMinutes,
        servings = servings,
        sourceUrl = sourceUrl,
        summary = summary,
        instructions = instructions,
        ingredients = ingredients.map { it.toEntity() },
        isFavorite = isFavorite
    )
}
