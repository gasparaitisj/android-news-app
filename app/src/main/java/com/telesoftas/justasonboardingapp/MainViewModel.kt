package com.telesoftas.justasonboardingapp

import androidx.lifecycle.ViewModel
import com.telesoftas.justasonboardingapp.utils.PreferencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject internal constructor(
    private val preferencesStore: PreferencesStore
) : ViewModel() {
    val isFirstLaunch = preferencesStore.isFirstLaunch()

    suspend fun updateIsFirstLaunch(isFirstLaunch: Boolean) {
        preferencesStore.updateIsFirstLaunch(isFirstLaunch)
    }
}
