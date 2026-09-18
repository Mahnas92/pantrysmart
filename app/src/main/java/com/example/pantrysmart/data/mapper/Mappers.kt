package com.example.pantrysmart.data.mapper

import com.example.pantrysmart.data.api.model.IngredientDto
import com.example.pantrysmart.data.api.model.RecipeDto
import com.example.pantrysmart.data.local.entity.IngredientEntity
import com.example.pantrysmart.data.local.entity.RecipeEntity
import com.example.pantrysmart.data.local.entity.ShoppingListItemEntity
import com.example.pantrysmart.domain.model.Ingredient
import com.example.pantrysmart.domain.model.Recipe

fun extractAdditionalInfo(original: String, amount: Double, unit: String, name: String): String? {
    var result = original
    
    val amtInt = amount.toInt()
    val amtStrInt = amtInt.toString()
    val amtStrDouble = amount.toString()
    
    if (unit.isNotBlank()) {
        result = result.replace(unit, "", ignoreCase = true)
    }
    
    result = result.replace(amtStrDouble, "", ignoreCase = true)
    result = result.replace(amtStrInt, "", ignoreCase = true)
    
    if (name.isNotBlank()) {
        result = result.replace(name, "", ignoreCase = true)
    }
    
    val parts = result.split("\\s+".toRegex()).filter { it.isNotBlank() }
    return if (parts.isEmpty()) null else parts.joinToString(", ")
}

fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(
        id = id,
        name = name,
        original = original,
        amount = amount,
        unit = unit,
        image = image,
        additionalInfo = extractAdditionalInfo(original, amount, unit, name)
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
        image = image,
        additionalInfo = additionalInfo
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
        image = image,
        additionalInfo = additionalInfo
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
        image = null,
        additionalInfo = if (additionalInfo.isBlank()) null else additionalInfo
    )
}

fun Ingredient.toShoppingListItemEntity(): ShoppingListItemEntity {
    return ShoppingListItemEntity(
        name = name,
        additionalInfo = additionalInfo ?: "",
        amount = amount,
        unit = unit
    )
}
