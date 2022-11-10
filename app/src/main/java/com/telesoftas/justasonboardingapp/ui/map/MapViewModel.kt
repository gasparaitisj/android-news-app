package com.telesoftas.justasonboardingapp.ui.map

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
) : ViewModel() {
    val state: MutableStateFlow<MapState> = MutableStateFlow(MapState())
}
