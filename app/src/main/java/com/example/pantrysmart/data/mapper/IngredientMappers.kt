package com.example.pantrysmart.data.mapper

import com.example.pantrysmart.data.api.model.IngredientDto
import com.example.pantrysmart.data.local.entity.IngredientEntity
import com.example.pantrysmart.data.local.entity.ShoppingListItemEntity
import com.example.pantrysmart.domain.model.Ingredient

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
    val fullImageUrl = if (image != null) {
        if (!image.startsWith("http")) {
            "https://spoonacular.com/cdn/ingredients_100x100/$image"
        } else {
            image.replace("http://", "https://")
        }
    } else {
        null
    }
    return Ingredient(
        id = id,
        name = name,
        original = original,
        amount = amount,
        unit = unit,
        image = fullImageUrl,
        additionalInfo = extractAdditionalInfo(original, amount, unit, name)
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
