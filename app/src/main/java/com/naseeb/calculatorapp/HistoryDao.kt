package com.naseeb.calculatorapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(history: HistoryEntity)

    @Query("SELECT * FROM history_table ORDER BY id DESC")
    suspend fun getAllHistory(): List<HistoryEntity>

    @Query("DELETE FROM history_table")
    suspend fun clearHistory()
}
