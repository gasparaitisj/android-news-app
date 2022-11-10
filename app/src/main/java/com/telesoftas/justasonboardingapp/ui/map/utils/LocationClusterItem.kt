package com.telesoftas.justasonboardingapp.ui.map.utils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class LocationClusterItem(
    val itemPosition: LatLng,
    val itemTitle: String,
    val itemSnippet: String,
    val successfulLandings: String? = null,
    val attemptedLandings: String? = null
) : ClusterItem {
    override fun getPosition(): LatLng = itemPosition
    override fun getTitle(): String = itemTitle
    override fun getSnippet(): String = itemSnippet
}
