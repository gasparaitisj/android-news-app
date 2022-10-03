package com.telesoftas.justasonboardingapp.sourcelist

import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlesListResponse

object SourceListFactory {
    fun create(response: Resource<ArticlesListResponse>): Resource<List<NewsSource>> {
        when (response.status) {
            Status.SUCCESS -> {
                response.data?.articles?.let { articles ->
                    return Resource.success(data = articles.map { articlePreviewResponse ->
                        NewsSource(
                            title = articlePreviewResponse.title ?: "",
                            description = articlePreviewResponse.description ?: ""
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
