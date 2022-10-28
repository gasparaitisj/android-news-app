package com.telesoftas.justasonboardingapp.utils.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface NewsSourceDao {
    @Query("SELECT * FROM news_source")
    fun getAllNewsSources(): Single<List<NewsSourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewsSources(newsSources: List<NewsSourceEntity>): Completable
}
