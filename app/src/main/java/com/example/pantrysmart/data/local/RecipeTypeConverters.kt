package com.example.pantrysmart.data.local

import androidx.room.TypeConverter
import com.example.pantrysmart.data.local.entity.IngredientEntity
import kotlinx.serialization.json.Json

class RecipeTypeConverters {
    @TypeConverter
    fun fromIngredientList(value: List<IngredientEntity>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toIngredientList(value: String): List<IngredientEntity> {
        return Json.decodeFromString(value)
    }
}
