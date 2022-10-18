package com.telesoftas.justasonboardingapp.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.compose.*

@MapsComposeExperimentalApi
@Composable
fun MapScreen(
    navController: NavHostController,
    viewModel: MapViewModel = hiltViewModel()
) {
    val defaultCameraPosition = CameraPosition.fromLatLngZoom(
        LatLng(55.92930340811748, 23.306731553438404), 11f
    )
    GoogleMapClustering(
        items = viewModel.locations,
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = rememberCameraPositionState { position = defaultCameraPosition }
    )
}

@MapsComposeExperimentalApi
@Composable
fun GoogleMapClustering(
    items: List<LocationItem>,
    modifier: Modifier,
    cameraPositionState: CameraPositionState
) {
    val context = LocalContext.current
    var clusterManager by remember { mutableStateOf<ClusterManager<LocationItem>?>(null) }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState
    ) {
        MapEffect(items) { map ->
            clusterManager = ClusterManager<LocationItem>(context, map)
            clusterManager?.addItems(items)
        }
        LaunchedEffect(key1 = cameraPositionState.isMoving) {
            if (!cameraPositionState.isMoving) {
                clusterManager?.onCameraIdle()
            }
        }
    }
}
