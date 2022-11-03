package com.telesoftas.justasonboardingapp.ui.newsdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.repository.ArticlesRepository
import com.telesoftas.justasonboardingapp.utils.repository.LocationRepository
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

    private val articleFromDatabase: StateFlow<Article?> =
        articlesRepository.getArticleByIdFromDatabase(id).stateIn(
            scope = viewModelScope,
            initialValue = null,
            started = SharingStarted.WhileSubscribed()
        )

    val location = locationRepository.getLocations()[0]

    init {
        viewModelScope.launch {
            articleFromDatabase.collectLatest {
                getArticle(it)
            }
        }
    }

    private fun getArticle(articleFromDatabase: Article?) {
        viewModelScope.launch {
            _article.value = Resource.loading()
            val response = articlesRepository.getArticleById(id)
            if (response.status == Status.ERROR) {
                _article.value = Resource.success(articleFromDatabase)
            } else {
                _article.value = response.copy(
                    data = response.data?.copy(isFavorite = articleFromDatabase?.isFavorite ?: false)
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
