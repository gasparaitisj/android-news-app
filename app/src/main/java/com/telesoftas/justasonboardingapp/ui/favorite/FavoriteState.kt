package com.telesoftas.justasonboardingapp.ui.favorite

import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.network.Resource

data class FavoriteState(
    val articles: Resource<List<Article>> = Resource.success(),
    val filteredArticles: List<Article> = listOf(),
    val searchWidgetState: SearchWidgetState = SearchWidgetState.CLOSED,
    val searchTextState: String = "",
)

enum class SearchWidgetState {
    OPENED,
    CLOSED
}
