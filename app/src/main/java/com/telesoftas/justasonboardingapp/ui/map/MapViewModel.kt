package com.telesoftas.justasonboardingapp.ui.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    locationRepository: LocationRepository
) : ViewModel() {
    val locations = locationRepository.getLocations()
    val defaultCameraPosition = CameraPosition.fromLatLngZoom(
        LatLng(54.897790620137464, 23.913658590073002), 6f
    )
}
