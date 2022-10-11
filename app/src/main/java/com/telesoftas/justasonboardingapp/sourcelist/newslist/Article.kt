package com.telesoftas.justasonboardingapp.sourcelist.newslist

import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory

data class Article(
    val id: String,
    val isFavorite: Boolean,
    val publishedAt: String,
    val source: String?,
    val category: ArticleCategory,
    val author: String?,
    val title: String?,
    val description: String?,
    val imageUrl: String?
) {
    fun toArticleEntity(): ArticleEntity? {
        id.toIntOrNull()?.let { idInt ->
            return ArticleEntity(
                id = idInt,
                isFavorite = isFavorite,
                publishedAt = publishedAt,
                source = source,
                category = category.ordinal,
                author = author,
                title = title,
                description = description,
                imageUrl = imageUrl
            )
        }
        return null
    }
}
