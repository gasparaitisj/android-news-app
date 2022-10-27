package com.telesoftas.justasonboardingapp.ui.about

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.utils.preferences.PreferencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val preferencesStore: PreferencesStore
) : ViewModel() {
    val savedPhotoUri: StateFlow<Uri?> = preferencesStore.getSavedPhotoUri().stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.WhileSubscribed()
    )
    fun updateSavedPhotoUri(uri: Uri) {
        viewModelScope.launch {
            preferencesStore.updateSavedPhotoUri(uri)
        }
    }
}
