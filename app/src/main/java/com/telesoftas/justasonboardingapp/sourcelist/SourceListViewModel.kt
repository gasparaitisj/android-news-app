package com.telesoftas.justasonboardingapp.sourcelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlesListResponse
import com.telesoftas.justasonboardingapp.utils.network.data.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourceListViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {
    private val _articles: MutableStateFlow<Resource<ArticlesListResponse>> =
        MutableStateFlow(Resource.loading())
    val articles: StateFlow<Resource<ArticlesListResponse>> = _articles.asStateFlow()

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
            _articles.value = Resource.loading()
            _articles.value = articlesRepository.getArticles(
                query = query,
                page = page,
                pageSize = pageSize,
                category = category,
                sortBy = sortBy,
                pageNumber = pageNumber,
                xRequestId = xRequestId
            )
        }
    }

    fun sortArticles(sortBy: SortBy) {
        when(sortBy.ordinal) {
            SortBy.ASCENDING.ordinal -> {
                val data = _articles.value.data?.copy(
                    articles = _articles.value.data?.articles?.sortedBy { it.title }
                )
                _articles.update {
                    it.copy(data = data)
                }
            }
            SortBy.DESCENDING.ordinal -> {
                val data = _articles.value.data?.copy(
                    articles = _articles.value.data?.articles?.sortedByDescending { it.title }
                )
                _articles.update {
                    it.copy(data = data)
                }
            }
        }
    }
}
