package com.telesoftas.justasonboardingapp.ui.sourcelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.utils.network.data.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourceListViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {
    private val _newsSources: MutableLiveData<List<NewsSource>> = MutableLiveData(listOf())
    val newsSources: LiveData<List<NewsSource>> = _newsSources

    private val _sortType: MutableLiveData<SortBy> = MutableLiveData(SortBy.NONE)
    val sortType: LiveData<SortBy> = _sortType

    private val _loadingState: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.LOADING)
    val loadingState: LiveData<LoadingState> = _loadingState

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        getArticles()
    }

    fun getArticles() {
        _loadingState.postValue(LoadingState.LOADING)
        articlesRepository
            .getNewsSourcesRx()
            .subscribeOn(Schedulers.io())
            .doAfterTerminate { _loadingState.postValue(LoadingState.SUCCESS) }
            .subscribe({ onGetArticlesSuccess(it) }, { onGetArticlesError() })
            .addTo(compositeDisposable)
    }

    private fun onGetArticlesError() {
        viewModelScope.launch {
            _newsSources.postValue(articlesRepository.getNewsSourcesFromDatabase().map { it.toNewsSource() })
        }
    }

    private fun onGetArticlesSuccess(newsSources: List<NewsSource>) {
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
            getArticles()
        }
    }

    private fun cacheNewsSources(newsSources: List<NewsSource>) {
        viewModelScope.launch {
            articlesRepository.insertNewsSourcesToDatabase(newsSources.map { it.toEntity() })
        }
    }
}

enum class LoadingState {
    LOADING,
    ERROR,
    SUCCESS
}
