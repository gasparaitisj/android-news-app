package com.telesoftas.justasonboardingapp.favorite

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {
    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> = mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState

    private val _articles: MutableStateFlow<Resource<List<Article>>> =
        MutableStateFlow(Resource.loading())
    val articles: StateFlow<Resource<List<Article>>> = _articles.asStateFlow()

    private val _filteredArticles: MutableStateFlow<List<Article>> = MutableStateFlow(emptyList())
    val filteredArticles: StateFlow<List<Article>> = _filteredArticles.asStateFlow()

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            _articles.value = Resource.loading()
            val response = articlesRepository.getFavoriteArticlesFromDatabase()
            _articles.value = FavoriteFactory().mapEntitiesToResource(response)
        }
    }

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }

    fun onFilterArticles(text: String) {
        _filteredArticles.value = articles.value.data?.filter { article ->
            article.title?.lowercase()?.contains(text.lowercase()) == true
        } ?: listOf()
    }

    fun onArticleFavoriteChanged(article: Article, isFavorite: Boolean) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = isFavorite))
        }
    }
}

enum class SearchWidgetState {
    OPENED,
    CLOSED
}
