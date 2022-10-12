package com.telesoftas.justasonboardingapp.utils.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsSourceDao {
    @Query("SELECT * FROM news_source")
    suspend fun getAllNewsSources(): List<NewsSourceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsSources(newsSources: List<NewsSourceEntity>)
}
