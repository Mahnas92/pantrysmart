package com.example.pantrysmart.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pantrysmart.data.local.dao.RecipeDao
import com.example.pantrysmart.data.local.dao.SearchHistoryDao
import com.example.pantrysmart.data.local.dao.ShoppingListDao
import com.example.pantrysmart.data.local.entity.RecipeEntity
import com.example.pantrysmart.data.local.entity.SearchHistoryEntity
import com.example.pantrysmart.data.local.entity.ShoppingListItemEntity

@Database(
    entities = [
        RecipeEntity::class,
        ShoppingListItemEntity::class,
        SearchHistoryEntity::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(RecipeTypeConverters::class)
abstract class PantryDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        @Volatile
        private var Instance: PantryDatabase? = null

        fun getDatabase(context: Context): PantryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    PantryDatabase::class.java,
                    "pantry_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
