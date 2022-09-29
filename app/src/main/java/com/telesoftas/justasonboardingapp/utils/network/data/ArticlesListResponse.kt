package com.telesoftas.justasonboardingapp.utils.network.data

data class ArticlesListResponse (
    val totalArticles: Long,
    val currentPage: Long,
    val totalPages: Long,
    val pageSize: Long,
    val articles: List<ArticlePreviewResponse>?
)
