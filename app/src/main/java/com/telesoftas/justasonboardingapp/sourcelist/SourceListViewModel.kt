package com.telesoftas.justasonboardingapp.sourcelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.network.data.SortBy
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
        MutableStateFlow(Resource.loading())
    val newsSources: StateFlow<Resource<List<NewsSource>>> = _newsSources.asStateFlow()

    private val _sortType: MutableStateFlow<SortBy> = MutableStateFlow(SortBy.NONE)
    val sortType: StateFlow<SortBy> = _sortType.asStateFlow()

    init {
        getArticles()
    }

    fun getArticles(
        query: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        category: ArticleCategory? = null,
        sortBy: String? = null,
        pageNumber: Int? = null,
        xRequestId: String? = null
    ) {
        viewModelScope.launch {
            _newsSources.value = Resource.loading()
            val response = articlesRepository.getArticles(
                query = query,
                page = page,
                pageSize = pageSize,
                category = category,
                sortBy = sortBy,
                pageNumber = pageNumber,
                xRequestId = xRequestId
            )
            _newsSources.value = SourceListFactory().create(response)
        }
    }

    fun sortArticles(sortBy: SortBy) {
        if (_sortType.value == SortBy.NONE) {
            _sortType.value = sortBy
            when(sortBy.ordinal) {
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
            getArticles()
        }
    }
}
