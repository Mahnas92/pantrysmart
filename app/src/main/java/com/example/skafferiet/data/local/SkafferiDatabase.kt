package com.example.skafferiet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.skafferiet.data.local.dao.RecipeDao
import com.example.skafferiet.data.local.entity.RecipeEntity

@Database(entities = [RecipeEntity::class], version = 1, exportSchema = true)
@TypeConverters(RecipeTypeConverters::class)
abstract class SkafferiDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var Instance: SkafferiDatabase? = null

        fun getDatabase(context: Context): SkafferiDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    SkafferiDatabase::class.java,
                    "skafferi_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
