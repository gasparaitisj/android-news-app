package com.telesoftas.justasonboardingapp.sourcelist.newslist

import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory

data class Article(
    val id: String,
    val publishedAt: String,
    val source: String? = null,
    val category: ArticleCategory,
    val author: String? = null,
    val title: String? = null,
    val description: String? = null,
    val imageUrl: String? = null
)
