package com.telesoftas.justasonboardingapp.sourcelist.newslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {
    private val _articles: MutableStateFlow<Resource<List<Article>>> =
        MutableStateFlow(Resource.loading())
    val articles: StateFlow<Resource<List<Article>>> = _articles.asStateFlow()

    private val favoriteArticles: StateFlow<List<ArticleEntity>> =
        articlesRepository.getFavoriteArticlesFromDatabase().stateIn(
            scope = viewModelScope,
            initialValue = listOf(),
            started = SharingStarted.WhileSubscribed()
        )

    private val _categoryType: MutableStateFlow<ArticleCategory> = MutableStateFlow(ArticleCategory.NONE)
    val categoryType: StateFlow<ArticleCategory> = _categoryType.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteArticles.collectLatest {
                onRefresh()
            }
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            _articles.value = Resource.loading()
            val response = articlesRepository.getArticles()
            if (response.status == Status.ERROR) {
                _articles.update { NewsListFactory().mapEntitiesToResource(articlesRepository.getArticlesFromDatabase()) }
            } else {
                _articles.update { NewsListFactory().mapResponseToResource(response, favoriteArticles.value) }
                cacheArticles()
            }
        }
    }

    fun onCategoryTypeChanged(categoryType: ArticleCategory) {
        if (_categoryType.value == ArticleCategory.NONE) {
            _categoryType.value = categoryType
            _articles.update { resource ->
                resource.copy(
                    data = _articles.value.data?.filter { it.category == categoryType }
                )
            }
        } else {
            _categoryType.value = ArticleCategory.NONE
            onRefresh()
        }
    }

    fun onArticleFavoriteChanged(article: Article, isFavorite: Boolean) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = isFavorite))
        }
    }

    private fun cacheArticles() {
        viewModelScope.launch {
            articlesRepository.insertArticlesToDatabase(
                NewsListFactory().mapResourceToEntity(articles.value)
            )
        }
    }
}
