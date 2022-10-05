package com.telesoftas.justasonboardingapp.utils.network.data

import com.squareup.moshi.Json

data class ArticlesListResponse (
    @Json(name="totalArticles")
    val totalArticles: Long,

    @Json(name="currentPage")
    val currentPage: Long,

    @Json(name="totalPages")
    val totalPages: Long,

    @Json(name="pageSize")
    val pageSize: Long,

    @Json(name="articles")
    val articles: List<ArticlePreviewResponse>?
)
