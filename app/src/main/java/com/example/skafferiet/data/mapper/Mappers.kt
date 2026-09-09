package com.example.skafferiet.data.mapper

import com.example.skafferiet.data.api.model.IngredientDto
import com.example.skafferiet.data.api.model.RecipeDto
import com.example.skafferiet.data.local.entity.IngredientEntity
import com.example.skafferiet.data.local.entity.RecipeEntity
import com.example.skafferiet.domain.model.Ingredient
import com.example.skafferiet.domain.model.Recipe

fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(
        id = id,
        name = name,
        original = original,
        amount = amount,
        unit = unit,
        image = image
    )
}

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = id.toLong(),
        title = title,
        image = image,
        readyInMinutes = readyInMinutes,
        servings = servings,
        sourceUrl = sourceUrl,
        ingredients = extendedIngredients?.map { it.toDomain() } ?: emptyList(),
        summary = summary,
        instructions = instructions
    )
}

fun IngredientEntity.toDomain(): Ingredient {
    return Ingredient(
        id = null,
        name = name,
        original = "$amount $unit $name",
        amount = amount,
        unit = unit,
        image = null
    )
}

fun RecipeEntity.toDomain(): Recipe {
    return Recipe(
        id = id,
        title = title,
        image = image,
        readyInMinutes = null,
        servings = null,
        sourceUrl = null,
        ingredients = ingredients.map { it.toDomain() },
        summary = summary,
        instructions = null
    )
}

fun Ingredient.toEntity(): IngredientEntity {
    return IngredientEntity(
        name = name,
        amount = amount,
        unit = unit
    )
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        title = title,
        image = image,
        summary = summary,
        ingredients = ingredients.map { it.toEntity() }
    )
}
