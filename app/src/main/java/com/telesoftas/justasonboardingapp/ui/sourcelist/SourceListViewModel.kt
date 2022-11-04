package com.telesoftas.justasonboardingapp.ui.sourcelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.SortBy
import com.telesoftas.justasonboardingapp.utils.repository.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourceListViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {
    private val _newsSources: MutableStateFlow<Resource<List<NewsSource>>> =
        MutableStateFlow(Resource.success())
    val newsSources: StateFlow<Resource<List<NewsSource>>> = _newsSources.asStateFlow()

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sortType: MutableStateFlow<SortBy> = MutableStateFlow(SortBy.NONE)
    val sortType: StateFlow<SortBy> = _sortType.asStateFlow()

    init {
        getNewsSources()
    }

    fun getNewsSources() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = articlesRepository.getNewsSources()
            _newsSources.value = response
            _isLoading.value = false
        }
    }

    fun sortArticles(sortBy: SortBy) {
        if (_sortType.value == SortBy.NONE) {
            _sortType.value = sortBy
            when (sortBy.ordinal) {
                SortBy.ASCENDING.ordinal -> {
                    _newsSources.value = _newsSources.value.copy(
                        data = _newsSources.value.data?.sortedBy { it.title }
                    )
                }
                SortBy.DESCENDING.ordinal -> {
                    _newsSources.value = _newsSources.value.copy(
                        data = _newsSources.value.data?.sortedByDescending { it.title }
                    )
                }
            }
        } else {
            _sortType.value = SortBy.NONE
            getNewsSources()
        }
    }
}
