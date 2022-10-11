package com.telesoftas.justasonboardingapp.newsdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
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
    private val _article: MutableStateFlow<Resource<Article>> =
        MutableStateFlow(Resource.loading())
    val article: StateFlow<Resource<Article>> = _article.asStateFlow()
    init {
        getArticle(id)
    }

    private fun getArticle(id: String) {
        viewModelScope.launch {
            _article.value = Resource.loading()
            val response = articlesRepository.getArticleById(id)
            val articleFromDatabase = articlesRepository.getArticleByIdFromDatabase(id)
            val isFavorite = articleFromDatabase?.isFavorite ?: false
            if (response.status == Status.ERROR) {
                if (articleFromDatabase != null) {
                    _article.value = NewsDetailsFactory().mapEntityToResource(articleFromDatabase)
                } else {
                    // notify view of empty state
                }
            } else {
                _article.value = NewsDetailsFactory().mapResponseToResource(response, isFavorite)
            }
        }
    }

    fun onArticleFavoriteChanged(article: Article, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                articlesRepository.insertArticleToDatabase(article.copy(isFavorite = true))
            } else {
                articlesRepository.deleteArticleByIdFromDatabase(article.id)
            }
        }
    }
}
