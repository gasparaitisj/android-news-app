package com.telesoftas.justasonboardingapp.ui.sourcelist.newslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.telesoftas.justasonboardingapp.ui.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.ui.sourcelist.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    private val firebaseAnalytics: FirebaseAnalytics
) : ViewModel() {
    private val _articles: MutableLiveData<List<Article>> = MutableLiveData(listOf())
    val articles: LiveData<List<Article>> = _articles

    private val _category: MutableLiveData<ArticleCategory> = MutableLiveData(ArticleCategory.NONE)
    val category: LiveData<ArticleCategory> = _category

    private val _status: MutableLiveData<Status> = MutableLiveData(Status.LOADING)
    val status: LiveData<Status> = _status

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var favoriteArticles: List<Article> = listOf()

    init {
        articlesRepository
            .getFavoriteArticlesFromDatabase()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    favoriteArticles = it
                    onRefresh()
                },
                Timber::e
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun onRefresh() {
        articlesRepository
            .getArticles()
            .map { mappedArticles ->
                mappedArticles.map { mappedArticle ->
                    val favoriteArticleById = favoriteArticles.firstOrNull { mappedArticle.id == it.id }
                    if (favoriteArticleById != null) mappedArticle.copy(isFavorite = true)
                    else mappedArticle
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
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
        articlesRepository
            .getArticlesFromDatabase()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ _articles.postValue(it) }, { Timber.d(it) })
            .addTo(compositeDisposable)
    }

    fun onCategoryTypeChanged(category: ArticleCategory) {
        if (_category.value == ArticleCategory.NONE) {
            _category.value = category
            _articles.postValue(_articles.value?.filter { it.category == category })
        } else {
            _category.value = ArticleCategory.NONE
            onRefresh()
        }
    }

    fun onArticleFavoriteChanged(article: Article, isFavorite: Boolean) {
        articlesRepository
            .insertArticleToDatabase(article.copy(isFavorite = isFavorite))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({}, Timber::e)
            .addTo(compositeDisposable)
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
        articlesRepository
            .insertArticlesToDatabase(articles.map { it.toArticleEntity() })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({}, Timber::e)
            .addTo(compositeDisposable)
    }
}
