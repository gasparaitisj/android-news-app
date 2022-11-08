package com.telesoftas.justasonboardingapp.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.telesoftas.justasonboardingapp.utils.repository.LandpadsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val landpadsRepository: LandpadsRepository
) : ViewModel() {
    val state: MutableStateFlow<MapState> = MutableStateFlow(MapState())

    init {
        loadLandpads()
    }

    private fun loadLandpads() {
        viewModelScope.launch {
            state.update { it.copy(landpadLocations = landpadsRepository.getLandpads()) }
            Timber.d("landpads = ${state.value.landpadLocations}")
        }
    }
}
