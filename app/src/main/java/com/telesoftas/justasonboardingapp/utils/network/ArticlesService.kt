package com.telesoftas.justasonboardingapp.utils.network

import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlePreviewResponse
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlesListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticlesService {
    @Headers("Content-Type: application/json")
    @GET("articles")
    suspend fun getArticles(
        @Query("q") query: String? = null,
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("category") category: ArticleCategory? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("order") pageNumber: Int? = null,
        @Query("x-request-id") xRequestId: String? = null
    ): Response<ArticlesListResponse>

    @Headers("Content-Type: application/json")
    @GET("articles/{id}")
    suspend fun getArticleById(
        @Path("id") id: String,
    ): Response<ArticlePreviewResponse>
}
