package com.example.pantrysmart.data.local.dao

import androidx.room.*
import com.example.pantrysmart.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistoryEntity)

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSearches(limit: Int = 5): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM search_history WHERE `query` NOT IN (SELECT `query` FROM (SELECT `query` FROM search_history ORDER BY timestamp DESC LIMIT :limit))")
    suspend fun trimHistory(limit: Int = 5)

    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun delete(query: String)

    @Transaction
    suspend fun insertAndTrim(query: String, limit: Int = 5) {
        insert(SearchHistoryEntity(query))
        trimHistory(limit)
    }
}
