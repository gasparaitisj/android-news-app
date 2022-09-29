package com.telesoftas.justasonboardingapp.sourcelist

import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.utils.network.ArticleApi
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleListResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticlesRepository @Inject constructor(
    private val articleApi: ArticleApi
) {
    suspend fun getArticles(
        query: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        category: ArticleCategory? = null,
        sortBy: String? = null,
        pageNumber: Int? = null,
        xRequestId: String? = null
    ): Resource<ArticleListResponse> {
        return try {
            val response = articleApi.getArticles(
                query = query,
                page = page,
                pageSize = pageSize,
                category = category,
                sortBy = sortBy,
                pageNumber = pageNumber,
                xRequestId = xRequestId
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error(msg = response.message())
            } else {
                Resource.error(msg = response.message())
            }
        } catch (exception: Exception) {
            Resource.error(msgRes = R.string.network_error)
        }
    }
}
