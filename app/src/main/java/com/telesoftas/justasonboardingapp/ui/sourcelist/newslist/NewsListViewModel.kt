package com.telesoftas.justasonboardingapp.ui.sourcelist.newslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.telesoftas.justasonboardingapp.ui.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.ui.sourcelist.Status
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    private val firebaseAnalytics: FirebaseAnalytics
) : ViewModel() {
    private val _articles: MutableLiveData<List<Article>> = MutableLiveData(listOf())
    val articles: LiveData<List<Article>> = _articles

    private val favoriteArticles: StateFlow<List<ArticleEntity>> =
        articlesRepository.getFavoriteArticlesFromDatabase().stateIn(
            scope = viewModelScope,
            initialValue = listOf(),
            started = SharingStarted.WhileSubscribed()
        )

    private val _categoryType: MutableLiveData<ArticleCategory> = MutableLiveData(ArticleCategory.NONE)
    val categoryType: LiveData<ArticleCategory> = _categoryType

    private val _status: MutableLiveData<Status> = MutableLiveData(Status.LOADING)
    val status: LiveData<Status> = _status
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        viewModelScope.launch {
            favoriteArticles.collectLatest {
                onRefresh()
            }
        }
    }

    fun onRefresh() {
        articlesRepository
            .getArticles()
            .map { articles ->
                articles.map { article ->
                    val favoriteArticleById = favoriteArticles.value.firstOrNull { article.id == it.id.toString() }
                    if (favoriteArticleById != null) article.copy(isFavorite = true) else article
                }
            }
            .subscribeOn(Schedulers.io())
            .doAfterTerminate { _status.postValue(Status.SUCCESS) }
            .subscribe({ onSuccess(it) }, { onError() })
            .addTo(compositeDisposable)
    }

    private fun onSuccess(articles: List<Article>) {
        _articles.postValue(articles)
        cacheArticles(articles)
    }

    private fun onError() {
        viewModelScope.launch {
            _articles.postValue(articlesRepository.getArticlesFromDatabase().map { it.toArticle() })
        }
    }

    fun onCategoryTypeChanged(categoryType: ArticleCategory) {
        if (_categoryType.value == ArticleCategory.NONE) {
            _categoryType.value = categoryType
            _articles.postValue(_articles.value?.filter { it.category == categoryType })
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

    fun onArticleClicked(article: Article) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, article.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, article.title ?: "")
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "news article")
            param(FirebaseAnalytics.Param.ITEM_CATEGORY, article.category.value)
        }
    }

    private fun cacheArticles(articles: List<Article>) {
        viewModelScope.launch {
            articlesRepository.insertArticlesToDatabase(articles.map { it.toArticleEntity() })
        }
    }
}
