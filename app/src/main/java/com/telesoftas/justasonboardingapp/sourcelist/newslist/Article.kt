package com.telesoftas.justasonboardingapp.sourcelist.newslist

import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory

data class Article(
    val id: String,
    val publishedAt: String,
    val source: String?,
    val category: ArticleCategory,
    val author: String?,
    val title: String?,
    val description: String?,
    val imageUrl: String?
)
