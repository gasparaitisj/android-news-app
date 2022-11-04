package com.telesoftas.justasonboardingapp.ui.sourcelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.SortBy
import com.telesoftas.justasonboardingapp.utils.repository.ArticlesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SourceListViewModel @Inject constructor(
    private val articlesRepository: ArticlesRepository
) : ViewModel() {
    val state: MutableStateFlow<SourceListState> = MutableStateFlow(SourceListState())
    val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    init {
        onRefresh()
    }

    fun onRefresh() {
        viewModelScope.launch {
            isLoading.value = true
            val response = articlesRepository.getNewsSources()
            state.update { it.copy(newsSources = response, sortType = SortBy.NONE) }
            isLoading.value = false
        }
    }

    fun sortArticles(sortBy: SortBy) {
        if (state.value.sortType == SortBy.NONE) {
            var updatedNewsSources: Resource<List<NewsSource>> = Resource.success()
            when (sortBy.ordinal) {
                SortBy.ASCENDING.ordinal -> {
                    updatedNewsSources = state.value.newsSources.copy(
                        data = state.value.newsSources.data?.sortedBy { it.title }
                    )
                }
                SortBy.DESCENDING.ordinal -> {
                    updatedNewsSources = state.value.newsSources.copy(
                        data = state.value.newsSources.data?.sortedByDescending { it.title }
                    )
                }
            }
            state.update { it.copy(newsSources = updatedNewsSources, sortType = sortBy) }
        } else {
            state.update { it.copy(sortType = SortBy.NONE) }
            onRefresh()
        }
    }
}
