package com.telesoftas.justasonboardingapp.ui.newsdetails

import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlePreviewResponse

class NewsDetailsFactory {
    fun mapResponseToResource(
        response: Resource<ArticlePreviewResponse>,
        isFavorite: Boolean
    ): Resource<Article> {
        when (response.status) {
            Status.SUCCESS -> {
                response.data?.let { articlePreviewResponse ->
                    return Resource.success(
                        data = Article(
                            id = articlePreviewResponse.id,
                            isFavorite = isFavorite,
                            publishedAt = articlePreviewResponse.publishedAt.replace(
                                "[\$TZ]".toRegex(),
                                " "
                            ),
                            source = articlePreviewResponse.source,
                            category = articlePreviewResponse.category,
                            author = articlePreviewResponse.author,
                            title = articlePreviewResponse.title,
                            description = articlePreviewResponse.description,
                            imageUrl = articlePreviewResponse.imageUrl,
                            votes = articlePreviewResponse.votes
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

    fun mapEntityToResource(entity: ArticleEntity): Resource<Article> {
        return Resource.success(
            Article(
                id = entity.id.toString(),
                isFavorite = entity.isFavorite,
                publishedAt = entity.publishedAt,
                source = entity.source,
                category = ArticleCategory.values()[entity.category],
                author = entity.author,
                title = entity.title,
                description = entity.description,
                imageUrl = entity.imageUrl,
                votes = entity.votes
            )
        )
    }
}
