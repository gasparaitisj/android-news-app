package com.telesoftas.justasonboardingapp

import androidx.lifecycle.ViewModel
import com.telesoftas.justasonboardingapp.utils.preferences.PreferencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesStore: PreferencesStore
) : ViewModel() {
    val isFirstLaunch = preferencesStore.isFirstLaunch()
    suspend fun setFirstLaunchCompleted() = preferencesStore.updateIsFirstLaunch(false)
}
