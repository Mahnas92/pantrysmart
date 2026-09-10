package com.example.skafferiet.data.mapper

import com.example.skafferiet.data.api.model.IngredientDto
import com.example.skafferiet.data.api.model.RecipeDto
import com.example.skafferiet.data.local.entity.IngredientEntity
import com.example.skafferiet.data.local.entity.RecipeEntity
import com.example.skafferiet.data.local.entity.ShoppingListItemEntity
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
    // TODO: Fix Image Loading - investigate why some images from Spoonacular are not loading
    val fullImageUrl = if (image != null && !image.startsWith("http")) {
        "https://spoonacular.com/recipeImages/$image"
    } else {
        image
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

fun IngredientEntity.toDomain(): Ingredient {
    return Ingredient(
        id = id,
        name = name,
        original = original,
        amount = amount,
        unit = unit,
        image = image
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

fun Ingredient.toEntity(): IngredientEntity {
    return IngredientEntity(
        id = id,
        name = name,
        original = original,
        amount = amount,
        unit = unit,
        image = image
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

fun ShoppingListItemEntity.toDomain(): Ingredient {
    return Ingredient(
        id = null,
        name = name,
        original = "$amount $unit $name",
        amount = amount,
        unit = unit,
        image = null
    )
}

fun Ingredient.toShoppingListItemEntity(): ShoppingListItemEntity {
    return ShoppingListItemEntity(
        name = name,
        amount = amount,
        unit = unit
    )
}
