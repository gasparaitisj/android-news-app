package com.telesoftas.justasonboardingapp.newsdetails

import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlePreviewResponse

class NewsDetailsFactory {
    fun create(response: Resource<ArticlePreviewResponse>): Resource<Article> {
        when (response.status) {
            Status.SUCCESS -> {
                response.data?.let { articlePreviewResponse ->
                    return Resource.success(
                        data = Article(
                            id = articlePreviewResponse.id,
                            isFavorite = false,
                            publishedAt = articlePreviewResponse.publishedAt.replace(
                                "[\$TZ]".toRegex(),
                                " "
                            ),
                            source = articlePreviewResponse.source,
                            category = articlePreviewResponse.category,
                            author = articlePreviewResponse.author,
                            title = articlePreviewResponse.title,
                            description = articlePreviewResponse.description,
                            imageUrl = articlePreviewResponse.imageUrl
                        )
                    )
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
