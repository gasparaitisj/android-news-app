package com.telesoftas.justasonboardingapp.utils.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ArticleDao {
    @Query("SELECT * FROM article")
    suspend fun getAllArticles(): List<Article>
}
