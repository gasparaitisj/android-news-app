package com.telesoftas.justasonboardingapp.sourcelist.newslist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.telesoftas.justasonboardingapp.utils.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val sourceTitle: String =
        checkNotNull(savedStateHandle[Screen.NewsList.KEY_TITLE])

}
