package com.telesoftas.justasonboardingapp.ui.favorite

import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory

class FavoriteFactory {
    fun mapEntitiesToResource(entities: List<ArticleEntity>): Resource<List<Article>> {
        return Resource.success(
            entities.map { entity ->
                Article(
                    id = entity.id.toString(),
                    isFavorite = entity.isFavorite,
                    publishedAt = entity.publishedAt,
                    source = entity.source,
                    category = ArticleCategory.values()[entity.category],
                    author = entity.author,
                    title = entity.title,
                    description = entity.description,
                    imageUrl = entity.imageUrl
                )
            }
        )
    }
}
