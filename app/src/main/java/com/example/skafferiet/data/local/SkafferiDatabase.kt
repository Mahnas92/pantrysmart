package com.example.skafferiet.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.skafferiet.data.local.dao.RecipeDao
import com.example.skafferiet.data.local.entity.RecipeEntity

@Database(entities = [RecipeEntity::class], version = 1, exportSchema = true)
@TypeConverters(RecipeTypeConverters::class)
abstract class SkafferiDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
}
