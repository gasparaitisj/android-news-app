package com.telesoftas.justasonboardingapp.ui.favorite

import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.ArticleViewData
import com.telesoftas.justasonboardingapp.utils.network.Resource

data class FavoriteState(
    val articles: Resource<List<ArticleViewData>> = Resource.success(),
    val filteredArticles: List<ArticleViewData> = listOf(),
    val searchWidgetState: SearchWidgetState = SearchWidgetState.CLOSED,
    val searchTextState: String = "",
)

enum class SearchWidgetState {
    OPENED,
    CLOSED
}
