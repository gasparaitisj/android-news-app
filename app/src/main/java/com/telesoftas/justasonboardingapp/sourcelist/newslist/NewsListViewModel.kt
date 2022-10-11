package com.telesoftas.justasonboardingapp.sourcelist.newslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {
    private val _articles: MutableStateFlow<Resource<List<Article>>> =
        MutableStateFlow(Resource.loading())
    val articles: StateFlow<Resource<List<Article>>> = _articles.asStateFlow()

    private val _categoryType: MutableStateFlow<ArticleCategory> = MutableStateFlow(ArticleCategory.NONE)
    val categoryType: StateFlow<ArticleCategory> = _categoryType.asStateFlow()

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            _articles.value = Resource.loading()
            val response = articlesRepository.getArticles()
            val favoriteArticles = articlesRepository.getFavoriteArticlesFromDatabase()
            _articles.value = NewsListFactory().mapResponseToResource(response, favoriteArticles)
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
            if (isFavorite) {
                articlesRepository.insertArticleToDatabase(article.copy(isFavorite = true))
            } else {
                articlesRepository.deleteArticleByIdFromDatabase(article.id)
            }
        }
    }
}
