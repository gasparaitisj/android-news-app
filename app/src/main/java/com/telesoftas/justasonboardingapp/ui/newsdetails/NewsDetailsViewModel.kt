package com.telesoftas.justasonboardingapp.ui.newsdetails

import androidx.lifecycle.SavedStateHandle
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
class NewsDetailsViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: String = checkNotNull(savedStateHandle["id"])

    private val _article: MutableStateFlow<Resource<Article>> = MutableStateFlow(Resource.success())
    val article: StateFlow<Resource<Article>> = _article

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = articlesRepository.getArticleById(id)
            _article.value = response
            _isLoading.value = false
        }
    }

    fun onArticleFavoriteChanged(article: Article) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = !article.isFavorite))
            onRefresh()
        }
    }
}
