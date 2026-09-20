package com.example.pantrysmart.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pantrysmart.data.local.entity.SearchResultEntity

@Dao
interface SearchResultDao {
    @Query("SELECT * FROM search_results WHERE searchKey = :searchKey")
    suspend fun getSearchResult(searchKey: String): SearchResultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResult(searchResult: SearchResultEntity)

    @Query("DELETE FROM search_results WHERE searchKey = :searchKey")
    suspend fun deleteSearchResult(searchKey: String)
}
