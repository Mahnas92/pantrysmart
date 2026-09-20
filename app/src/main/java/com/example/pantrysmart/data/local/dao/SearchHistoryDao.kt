package com.example.pantrysmart.data.local.dao

import androidx.room.*
import com.example.pantrysmart.data.local.entity.SearchHistoryEntity
import com.example.pantrysmart.util.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistoryEntity)

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSearches(limit: Int = Constants.SEARCH_HISTORY_LIMIT): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM search_history WHERE `query` NOT IN (SELECT `query` FROM (SELECT `query` FROM search_history ORDER BY timestamp DESC LIMIT :limit))")
    suspend fun trimHistory(limit: Int = Constants.SEARCH_HISTORY_LIMIT)

    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun delete(query: String)

    @Transaction
    suspend fun insertAndTrim(query: String, limit: Int = Constants.SEARCH_HISTORY_LIMIT) {
        insert(SearchHistoryEntity(query.trim()))
        trimHistory(limit)
    }
}
