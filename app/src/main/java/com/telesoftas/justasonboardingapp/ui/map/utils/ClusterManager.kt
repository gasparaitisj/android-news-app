package com.telesoftas.justasonboardingapp.ui.map.utils

import android.content.Context
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager

class ClusterManager(
    context: Context,
    viewGroup: ViewGroup,
    compositionContext: CompositionContext,
    map: GoogleMap,
    items: List<LocationClusterItem>,
    clusterInfoWindowContent: @Composable (Marker) -> Unit,
    clusterItemInfoWindowContent: @Composable (Marker) -> Unit,
    onClusterInfoWindowClicked: (Cluster<LocationClusterItem>) -> Unit = {},
    onClusterItemInfoWindowClicked: (LocationClusterItem) -> Unit = {}
) : ClusterManager<LocationClusterItem>(context, map) {
    var clickedCluster: Cluster<LocationClusterItem>? = null
    init {
        addItems(items)
        renderer = ClusterRenderer(
            context = context,
            map = map,
            clusterManager = this
        )
        clusterMarkerCollection.setInfoWindowAdapter(
            ClusterInfoWindowAdapter(
                viewGroup = viewGroup,
                compositionContext = compositionContext,
                content = clusterInfoWindowContent
            )
        )
        setOnClusterClickListener { cluster ->
            clickedCluster = cluster
            false
        }
        setOnClusterInfoWindowClickListener { cluster ->
            onClusterInfoWindowClicked(cluster)
        }
        markerCollection.setInfoWindowAdapter(
            ClusterInfoWindowAdapter(
                viewGroup = viewGroup,
                compositionContext = compositionContext,
                content = clusterItemInfoWindowContent
            )
        )
        setOnClusterItemInfoWindowClickListener { clusterItem ->
            onClusterItemInfoWindowClicked(clusterItem)
        }
    }
}
