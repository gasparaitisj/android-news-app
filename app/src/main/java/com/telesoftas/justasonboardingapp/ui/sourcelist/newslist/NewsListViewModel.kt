package com.telesoftas.justasonboardingapp.ui.sourcelist.newslist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.telesoftas.justasonboardingapp.utils.navigation.Screen
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.repository.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    private val firebaseAnalytics: FirebaseAnalytics,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val sourceTitle: String? = savedStateHandle[Screen.NewsList.KEY_TITLE]

    private val _articles: MutableStateFlow<Resource<List<Article>>> =
        MutableStateFlow(Resource.success())
    val articles: StateFlow<Resource<List<Article>>> = _articles.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _categoryType: MutableStateFlow<ArticleCategory> = MutableStateFlow(ArticleCategory.NONE)
    val categoryType: StateFlow<ArticleCategory> = _categoryType.asStateFlow()

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = articlesRepository.getArticles()
            _articles.value = response
            _isLoading.value = false
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

    fun onArticleFavoriteChanged(article: Article) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = !article.isFavorite))
            onRefresh()
        }
    }

    fun onArticleClicked(article: Article) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, article.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, article.title ?: "")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "news article")
            param(FirebaseAnalytics.Param.ITEM_CATEGORY, article.category.value)
        }
    }
}
