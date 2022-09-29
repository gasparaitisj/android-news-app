package com.telesoftas.justasonboardingapp.utils.network.data

data class ArticleListResponse (
    val totalArticles: Long,
    val currentPage: Long,
    val totalPages: Long,
    val pageSize: Long,
    val articlePreviewResponses: List<ArticlePreviewResponse>?
)
