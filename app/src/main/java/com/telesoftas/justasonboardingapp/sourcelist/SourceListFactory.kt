package com.telesoftas.justasonboardingapp.sourcelist

import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.utils.data.NewsSourceEntity
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlesListResponse


class SourceListFactory {
    fun mapResponseToResource(response: Resource<ArticlesListResponse>): Resource<List<NewsSource>> {
        when (response.status) {
            Status.SUCCESS -> {
                response.data?.articles?.let { articles ->
                    return Resource.success(data = articles.map { articlePreviewResponse ->
                        NewsSource(
                            id = articlePreviewResponse.id,
                            title = articlePreviewResponse.title ?: "",
                            description = articlePreviewResponse.description ?: ""
                        )
                    })
                }
                return Resource.success(data = emptyList())
            }
            Status.LOADING -> {
                return Resource.loading()
            }
            Status.ERROR -> {
                return Resource.error(msgRes = R.string.network_error)
            }
        }
    }

    fun mapResourceToEntity(resource: Resource<List<NewsSource>>): List<NewsSourceEntity> {
        return when (resource.status) {
            Status.SUCCESS -> {
                resource.data?.map { newsSource ->
                    NewsSourceEntity(
                        id = newsSource.id.toIntOrNull() ?: 0,
                        title = newsSource.title,
                        description = newsSource.description
                    )
                }.orEmpty()
            }
            else -> emptyList()
        }
    }

    fun mapEntitiesToResource(entities: List<NewsSourceEntity>): Resource<List<NewsSource>> {
        return Resource.success(
            entities.map { entity ->
                NewsSource(
                    id = entity.id.toString(),
                    title = entity.title,
                    description = entity.description
                )
            }
        )
    }
}
