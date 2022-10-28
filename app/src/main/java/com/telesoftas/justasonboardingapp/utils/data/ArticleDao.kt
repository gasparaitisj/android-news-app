package com.telesoftas.justasonboardingapp.utils.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface ArticleDao {
    @Query("SELECT * FROM article")
    suspend fun getAllArticles(): List<ArticleEntity>

    @Query("SELECT * FROM article WHERE id = :id")
    fun getArticleById(id: Int): Single<ArticleEntity>

    @Query("SELECT * FROM article WHERE is_favorite")
    fun getFavoriteArticles(): Flowable<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(article: ArticleEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticles(article: List<ArticleEntity>): Completable

    @Query("DELETE FROM article WHERE id = :id")
    fun deleteArticleById(id: Int): Completable
}
