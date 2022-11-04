package com.telesoftas.justasonboardingapp.ui.favorite

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.repository.ArticlesRepository
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

    private val _articles: MutableStateFlow<Resource<List<Article>>> = MutableStateFlow(Resource.success())
    val articles: StateFlow<Resource<List<Article>>> = _articles.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = articlesRepository.getFavoriteArticlesFromDatabase()
            _articles.value = Resource.success(response)
            _isLoading.value = false
        }
    }

    private val _filteredArticles: MutableStateFlow<List<Article>> = MutableStateFlow(emptyList())
    val filteredArticles: StateFlow<List<Article>> = _filteredArticles.asStateFlow()

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }

    fun onFilterArticles(text: String) {
        _filteredArticles.value = articles.value.getSuccessDataOrNull()?.filter { article ->
            article.title?.lowercase()?.contains(text.lowercase()) == true
        } ?: listOf()
    }

    fun onArticleFavoriteChanged(article: Article) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = !article.isFavorite))
            onRefresh()
        }
    }
}

enum class SearchWidgetState {
    OPENED,
    CLOSED
}
