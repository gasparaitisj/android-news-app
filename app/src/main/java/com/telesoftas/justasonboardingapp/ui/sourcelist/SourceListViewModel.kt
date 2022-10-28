package com.telesoftas.justasonboardingapp.ui.sourcelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.telesoftas.justasonboardingapp.utils.network.data.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SourceListViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {
    private val _newsSources: MutableLiveData<List<NewsSource>> = MutableLiveData(listOf())
    val newsSources: LiveData<List<NewsSource>> = _newsSources

    private val _sortType: MutableLiveData<SortBy> = MutableLiveData(SortBy.NONE)
    val sortType: LiveData<SortBy> = _sortType

    private val _status: MutableLiveData<Status> = MutableLiveData(Status.LOADING)
    val status: LiveData<Status> = _status
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        onRefresh()
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

    fun onRefresh() {
        _status.postValue(Status.LOADING)
        articlesRepository
            .getNewsSources()
            .subscribeOn(Schedulers.io())
            .doAfterTerminate { _status.postValue(Status.SUCCESS) }
            .subscribe({ onSuccess(it) }, { onError() })
            .addTo(compositeDisposable)
    }

    private fun onError() {
        articlesRepository
            .getNewsSourcesFromDatabase()
            .subscribeOn(Schedulers.io())
            .subscribe({ _newsSources.postValue(it) }, { Timber.d(it) })
            .addTo(compositeDisposable)
    }

    private fun onSuccess(newsSources: List<NewsSource>) {
        _newsSources.postValue(newsSources)
        cacheNewsSources(newsSources)
    }

    fun sortArticles(sortBy: SortBy) {
        if (_sortType.value == SortBy.NONE) {
            _sortType.postValue(sortBy)
            when (sortBy.ordinal) {
                SortBy.ASCENDING.ordinal -> {
                    _newsSources.postValue(_newsSources.value?.sortedBy { it.title })
                }
                SortBy.DESCENDING.ordinal -> {
                    _newsSources.postValue(_newsSources.value?.sortedByDescending { it.title })
                }
            }
        } else {
            _sortType.postValue(SortBy.NONE)
            onRefresh()
        }
    }

    private fun cacheNewsSources(newsSources: List<NewsSource>) {
        articlesRepository.insertNewsSourcesToDatabase(newsSources)
    }
}

enum class Status {
    LOADING,
    ERROR,
    SUCCESS
}
