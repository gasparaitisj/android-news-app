package com.telesoftas.justasonboardingapp.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.repository.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {
    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val state: MutableStateFlow<FavoriteState> = MutableStateFlow(FavoriteState())

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            isLoading.value = true
            val response = articlesRepository.getFavoriteArticlesFromDatabase()
            state.update { it.copy(articles = Resource.success(response)) }
            isLoading.value = false
        }
    }

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        state.update { it.copy(searchWidgetState = newValue) }
    }

    fun updateSearchTextState(newValue: String) {
        state.update { it.copy(searchTextState = newValue) }
    }

    fun onFilterArticles(text: String) {
        state.update {
            it.copy(
                filteredArticles = it.articles.getSuccessDataOrNull()?.filter { article ->
                    article.title?.lowercase()?.contains(text.lowercase()) == true
                } ?: listOf()
            )
        }
    }

    fun onArticleFavoriteChanged(article: Article) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = !article.isFavorite))
            onRefresh()
        }
    }
}
