package com.telesoftas.justasonboardingapp.ui.sourcelist.newslist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.telesoftas.justasonboardingapp.utils.navigation.Screen
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.repository.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    private val firebaseAnalytics: FirebaseAnalytics,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val sourceTitle: String = savedStateHandle[Screen.NewsList.KEY_TITLE] ?: ""
    val state: MutableStateFlow<NewsListState> = MutableStateFlow(NewsListState())
    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            isLoading.value = true
            val response = articlesRepository.getArticles()
            state.update {
                it.copy(
                    sourceTitle = sourceTitle,
                    articles = response,
                    categoryType = ArticleCategory.NONE
                )
            }
            isLoading.value = false
        }
    }

    fun onCategoryTypeChanged(categoryType: ArticleCategory) {
        if (state.value.categoryType == ArticleCategory.NONE) {
            state.update {
                it.copy(
                    articles = it.articles.copy(
                        data = it.articles.data?.filter { article -> article.category == categoryType }
                    ),
                    categoryType = categoryType
                )
            }
        } else {
            state.update { it.copy(categoryType = ArticleCategory.NONE) }
            onRefresh()
        }
    }

    fun onArticleFavoriteChanged(article: ArticleViewData) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = !article.isFavorite))
            onRefresh()
        }
    }

    fun onArticleClicked(article: ArticleViewData) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, article.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, article.title ?: "")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "news article")
            param(FirebaseAnalytics.Param.ITEM_CATEGORY, article.category.value)
        }
    }
}
