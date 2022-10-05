package com.telesoftas.justasonboardingapp.sourcelist.newslist

import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlesListResponse

class NewsListFactory {
    fun create(response: Resource<ArticlesListResponse>): Resource<List<Article>> {
        when (response.status) {
            Status.SUCCESS -> {
                response.data?.articles?.let { articles ->
                    return Resource.success(data = articles.map { articlePreviewResponse ->
                        Article(
                            id = articlePreviewResponse.id,
                            publishedAt = articlePreviewResponse.publishedAt,
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
}
