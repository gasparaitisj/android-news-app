package com.telesoftas.justasonboardingapp.utils.network.data

import com.telesoftas.justasonboardingapp.ui.map.utils.LocationClusterItem

data class Landpad(
    val location: Location,
    val fullName: String?,
    val successfulLandings: String?,
    val attemptedLandings: String?,
    val wikipedia: String?
) {
    fun toClusterItem(): LocationClusterItem? {
        location.toLatLng()?.let { location ->
            return LocationClusterItem(
                itemTitle = fullName ?: "",
                itemPosition = location,
                itemSnippet = wikipedia ?: ""
            )
        }
        return null
    }
}
