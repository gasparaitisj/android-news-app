package com.telesoftas.justasonboardingapp.ui.main

import androidx.lifecycle.ViewModel
import com.telesoftas.justasonboardingapp.utils.preferences.PreferencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesStore: PreferencesStore
) : ViewModel() {
    val isFirstLaunch: Flow<Boolean> = preferencesStore.isFirstLaunch()
    suspend fun setFirstLaunchCompleted() {
        preferencesStore.updateIsFirstLaunch(false)
    }
}
