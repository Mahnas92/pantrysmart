package com.example.pantrysmart.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pantrysmart.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeEntity>)

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: Long): Flow<RecipeEntity?>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeByIdOnce(id: Long): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE id IN (:ids)")
    fun getRecipesByIds(ids: List<Long>): Flow<List<RecipeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM recipes WHERE id = :id AND isFavorite = 1)")
    fun isFavorite(id: Long): Flow<Boolean>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: RecipeEntity)

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Delete
    suspend fun delete(recipe: RecipeEntity)

    @Query("DELETE FROM recipes")
    suspend fun deleteAll()
}
