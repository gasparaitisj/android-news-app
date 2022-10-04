package com.telesoftas.justasonboardingapp.sourcelist.newslist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.telesoftas.justasonboardingapp.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val sourceTitle: String =
        checkNotNull(savedStateHandle[Constants.Routes.NewsListArguments.title])

    init {
        Timber.d(sourceTitle)
    }
}
