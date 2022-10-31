package com.telesoftas.justasonboardingapp.ui.newsdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.ui.map.LocationRepository
import com.telesoftas.justasonboardingapp.ui.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsDetailsViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: String = checkNotNull(savedStateHandle["id"])

    private val _article: MutableStateFlow<Resource<Article>> = MutableStateFlow(Resource.loading())
    val article: StateFlow<Resource<Article>> = _article.asStateFlow()

    private val articleFromDatabase: StateFlow<ArticleEntity?> =
        articlesRepository.getArticleByIdFromDatabase(id).stateIn(
            scope = viewModelScope,
            initialValue = null,
            started = SharingStarted.WhileSubscribed()
        )

    val location = locationRepository.getLocations()[0]

    init {
        viewModelScope.launch {
            articleFromDatabase.collectLatest {
                getArticle()
            }
        }
    }

    private fun getArticle() {
        viewModelScope.launch {
            _article.value = Resource.loading()
            val response = articlesRepository.getArticleById(id)
            if (response.status == Status.ERROR) {
                articleFromDatabase.value?.let { articleEntity ->
                    _article.value = NewsDetailsFactory().mapEntityToResource(articleEntity)
                }
            } else {
                _article.value = NewsDetailsFactory().mapResponseToResource(
                    response = response,
                    isFavorite = articleFromDatabase.value?.isFavorite ?: false
                )
            }
        }
    }

    fun onArticleFavoriteChanged(article: Article, isFavorite: Boolean) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = isFavorite))
        }
    }
}
