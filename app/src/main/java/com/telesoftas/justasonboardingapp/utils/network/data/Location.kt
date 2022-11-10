package com.telesoftas.justasonboardingapp.utils.network.data

import com.google.android.gms.maps.model.LatLng

data class Location(
    val latitude: Double,
    val longitude: Double
) {
    fun toLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
}
