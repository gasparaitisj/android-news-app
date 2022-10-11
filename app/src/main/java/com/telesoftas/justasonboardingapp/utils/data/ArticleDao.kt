package com.telesoftas.justasonboardingapp.utils.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArticleDao {
    @Query("SELECT * FROM article")
    suspend fun getAllArticles(): List<ArticleEntity>

    @Query("SELECT * FROM article WHERE id = :id")
    suspend fun getArticleById(id: Int): ArticleEntity?

    @Query("SELECT * FROM article WHERE is_favorite")
    suspend fun getFavoriteArticles(): List<ArticleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: ArticleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(article: List<ArticleEntity>)

    @Query("DELETE FROM article WHERE id = :id")
    suspend fun deleteArticleById(id: Int)
}
