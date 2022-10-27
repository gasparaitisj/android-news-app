package com.telesoftas.justasonboardingapp.ui.newsdetails

import androidx.lifecycle.*
import com.telesoftas.justasonboardingapp.ui.map.LocationRepository
import com.telesoftas.justasonboardingapp.ui.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.ui.sourcelist.LoadingState
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsDetailsViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: String = checkNotNull(savedStateHandle["id"])

    private val _article: MutableLiveData<Article> = MutableLiveData()
    val article: LiveData<Article> = _article

    private val articleFromDatabase: Flow<ArticleEntity?> = articlesRepository.getArticleByIdFromDatabase(id)

    val location = locationRepository.getLocations()[0]

    private val _loadingState: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.LOADING)
    val loadingState: LiveData<LoadingState> = _loadingState
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        viewModelScope.launch {
            articleFromDatabase.collectLatest {
                getArticle(it)
            }
        }
    }

    private fun getArticle(articleEntity: ArticleEntity?) {
        articlesRepository
            .getArticleByIdRx(id)
            .map { it.copy(isFavorite = articleEntity?.isFavorite ?: false) }
            .subscribeOn(Schedulers.io())
            .doAfterTerminate { _loadingState.postValue(LoadingState.SUCCESS) }
            .subscribe({ onGetArticleSuccess(it) }, { onGetArticleError(articleEntity) })
            .addTo(compositeDisposable)
    }

    private fun onGetArticleError(articleEntity: ArticleEntity?) {
        _article.postValue(articleEntity?.toArticle())
    }

    private fun onGetArticleSuccess(article: Article) {
        _article.postValue(article)
    }

    fun onArticleFavoriteChanged(article: Article, isFavorite: Boolean) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = isFavorite))
        }
    }
}
