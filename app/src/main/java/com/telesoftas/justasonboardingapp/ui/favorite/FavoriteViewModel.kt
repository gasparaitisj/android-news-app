package com.telesoftas.justasonboardingapp.ui.favorite

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.ui.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.ui.sourcelist.Status
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {
    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> = mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState

    private val _articles: MutableLiveData<List<Article>> = MutableLiveData(listOf())
    val articles: LiveData<List<Article>> = _articles

    private val _filteredArticles: MutableLiveData<List<Article>> = MutableLiveData(emptyList())
    val filteredArticles: LiveData<List<Article>> = _filteredArticles

    private val _status: MutableLiveData<Status> = MutableLiveData(Status.LOADING)
    val status: LiveData<Status> = _status
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        articlesRepository
            .getFavoriteArticlesFromDatabase()
            .subscribeOn(Schedulers.io())
            .doAfterNext { _status.postValue(Status.SUCCESS) }
            .subscribe({ onRefresh(it) }, { Timber.d(it) })
            .addTo(compositeDisposable)
    }

    private fun onRefresh(articleList: List<Article>) {
        _status.postValue(Status.LOADING)
        _articles.postValue(articleList)
    }

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }

    fun onFilterArticles(text: String) {
        _filteredArticles.value = articles.value?.filter { article ->
            article.title?.lowercase()?.contains(text.lowercase()) == true
        } ?: listOf()
    }

    fun onArticleFavoriteChanged(article: Article, isFavorite: Boolean) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = isFavorite))
        }
    }
}

enum class SearchWidgetState {
    OPENED,
    CLOSED
}
