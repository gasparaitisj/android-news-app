package com.telesoftas.justasonboardingapp.utils.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.telesoftas.justasonboardingapp.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory

@Entity(tableName = "article")
data class ArticleEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean,
    @ColumnInfo(name = "published_at") val publishedAt: String,
    @ColumnInfo(name = "source") val source: String?,
    @ColumnInfo(name = "category") val category: Int,
    @ColumnInfo(name = "author") val author: String?,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
) {
    fun toArticle(): Article {
        return Article(
            id = id.toString(),
            isFavorite = isFavorite,
            publishedAt = publishedAt,
            source = source,
            category = ArticleCategory.values()[category],
            author = author,
            title = title,
            description = description,
            imageUrl = imageUrl
        )
    }
}
