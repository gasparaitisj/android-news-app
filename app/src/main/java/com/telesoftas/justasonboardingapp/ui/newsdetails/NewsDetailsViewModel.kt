package com.telesoftas.justasonboardingapp.ui.newsdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.ArticleViewData
import com.telesoftas.justasonboardingapp.utils.repository.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: String = checkNotNull(savedStateHandle["id"])
    val state: MutableStateFlow<NewsDetailsState> = MutableStateFlow(NewsDetailsState())
    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            isLoading.value = true
            val response = articlesRepository.getArticleById(id)
            state.update { it.copy(article = response) }
            isLoading.value = false
        }
    }

    fun onArticleFavoriteChanged(article: ArticleViewData) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = !article.isFavorite))
            onRefresh()
        }
    }
}
