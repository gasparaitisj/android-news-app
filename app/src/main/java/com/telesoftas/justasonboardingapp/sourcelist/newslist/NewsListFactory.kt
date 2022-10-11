package com.telesoftas.justasonboardingapp.sourcelist.newslist

import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlesListResponse

class NewsListFactory {
    fun mapResponseToResource(response: Resource<ArticlesListResponse>): Resource<List<Article>> {
        when (response.status) {
            Status.SUCCESS -> {
                response.data?.articles?.let { articles ->
                    return Resource.success(data = articles.map { articlePreviewResponse ->
                        Article(
                            id = articlePreviewResponse.id,
                            isFavorite = false,
                            publishedAt = articlePreviewResponse.publishedAt.replace("[\$TZ]".toRegex(), " "),
                            source = articlePreviewResponse.source,
                            category = articlePreviewResponse.category,
                            author = articlePreviewResponse.author,
                            title = articlePreviewResponse.title,
                            description = articlePreviewResponse.description,
                            imageUrl = articlePreviewResponse.imageUrl
                        )
                    })
                }
            }
            Status.LOADING -> {
                return Resource.loading()
            }
            Status.ERROR -> {
                return Resource.error(msgRes = R.string.network_error)
            }
        }
        return Resource.error(msgRes = R.string.unknown_error)
    }

    fun mapResourceToEntity(resource: Resource<List<Article>>): List<ArticleEntity> {
        return when (resource.status) {
            Status.SUCCESS -> {
                resource.data?.map { article ->
                    ArticleEntity(
                        id = article.id.toIntOrNull() ?: 0,
                        isFavorite = article.isFavorite,
                        publishedAt = article.publishedAt,
                        source = article.source,
                        category = article.category.ordinal,
                        author = article.author,
                        title = article.title,
                        description = article.description,
                        imageUrl = article.imageUrl
                    )
                }.orEmpty()
            }
            else -> emptyList()
        }
    }
}
