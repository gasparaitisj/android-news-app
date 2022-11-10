package com.telesoftas.justasonboardingapp.ui.about

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.utils.preferences.PreferencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val preferencesStore: PreferencesStore
) : ViewModel() {
    val state: MutableStateFlow<AboutState> = MutableStateFlow(AboutState())
    private val savedUri = preferencesStore.getSavedPhotoUri()

    init {
        viewModelScope.launch {
            savedUri.collectLatest { uri ->
                state.update { it.copy(savedPhotoUri = uri) }
            }
        }
    }

    fun updateSavedPhotoUri(uri: Uri) {
        viewModelScope.launch {
            preferencesStore.updateSavedPhotoUri(uri)
        }
    }
}
