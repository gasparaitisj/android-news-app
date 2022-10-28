package com.telesoftas.justasonboardingapp.ui.newsdetails

import androidx.lifecycle.*
import com.telesoftas.justasonboardingapp.ui.map.LocationRepository
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
class NewsDetailsViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository,
    private val locationRepository: LocationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: String = checkNotNull(savedStateHandle["id"])

    private val _article: MutableLiveData<Article> = MutableLiveData()
    val article: LiveData<Article> = _article

    val location = locationRepository.getLocations()[0]

    private val _status: MutableLiveData<Status> = MutableLiveData(Status.LOADING)
    val status: LiveData<Status> = _status
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        articlesRepository
            .getArticleByIdFromDatabase(id)
            .subscribeOn(Schedulers.io())
            .subscribe({ onRefresh(it) }, { Timber.d(it) })
            .addTo(compositeDisposable)
    }

    private fun onRefresh(article: Article) {
        articlesRepository
            .getArticleById(id)
            .map { it.copy(isFavorite = article.isFavorite) }
            .subscribeOn(Schedulers.io())
            .doAfterTerminate { _status.postValue(Status.SUCCESS) }
            .subscribe({ onSuccess(it) }, { onError(article) })
            .addTo(compositeDisposable)
    }

    private fun onError(article: Article) {
        _article.postValue(article)
    }

    private fun onSuccess(article: Article) {
        _article.postValue(article)
    }

    fun onArticleFavoriteChanged(article: Article, isFavorite: Boolean) {
        viewModelScope.launch {
            articlesRepository.insertArticleToDatabase(article.copy(isFavorite = isFavorite))
        }
    }
}
