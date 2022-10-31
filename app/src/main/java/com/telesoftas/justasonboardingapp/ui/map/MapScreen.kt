package com.telesoftas.justasonboardingapp.ui.map

import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.compose.*
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.map.utils.ClusterManager
import com.telesoftas.justasonboardingapp.ui.map.utils.LocationClusterItem
import com.telesoftas.justasonboardingapp.ui.theme.Typography

@MapsComposeExperimentalApi
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val locations = viewModel.locations
    val defaultCameraPosition = viewModel.defaultCameraPosition
    GoogleMapWithClustering(
        items = locations,
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = rememberCameraPositionState { position = defaultCameraPosition }
    )
}

@MapsComposeExperimentalApi
@Composable
fun GoogleMapWithClustering(
    items: List<LocationClusterItem>,
    modifier: Modifier,
    cameraPositionState: CameraPositionState,
    uiSettings: MapUiSettings = MapUiSettings()
) {
    val uriHandler = LocalUriHandler.current
    var clusterManager by remember { mutableStateOf<ClusterManager?>(null) }
    val context = LocalContext.current
    val viewGroup = LocalView.current as ViewGroup
    val compositionContext = rememberCompositionContext()

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings
    ) {
        MapEffect(items) { map ->
            clusterManager = ClusterManager(
                context = context,
                viewGroup = viewGroup,
                compositionContext = compositionContext,
                map = map,
                items = items,
                clusterItemInfoWindowContent = { marker ->
                    ClusterItemInfoWindow(marker)
                },
                clusterInfoWindowContent = {
                    ClusterInfoWindow(clusterManager?.clickedCluster)
                },
                onClusterItemInfoWindowClicked = { clusterItem ->
                    uriHandler.openUri(clusterItem.snippet)
                }
            )
        }
        LaunchedEffect(key1 = cameraPositionState.isMoving) {
            if (!cameraPositionState.isMoving) {
                clusterManager?.onCameraIdle()
            }
        }
    }
}

@Composable
fun ClusterInfoWindow(
    cluster: Cluster<LocationClusterItem>?
) {
    val locationText = if (cluster?.size != null) {
        "Gintarinė vaistinė (${cluster.size} locations)"
    } else "Gintarinė vaistinė"

    Surface(
        elevation = 3.dp,
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.8f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_gintarine_logo),
                contentDescription = "Gintarinė vaistinė"
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = locationText,
                style = Typography.subtitle1,
                color = Color.Black
            )
            cluster?.position?.let { latLng ->
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = String.format(
                        "%.5f, %.5f",
                        latLng.latitude, latLng.longitude
                    ),
                    style = Typography.subtitle2,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun ClusterItemInfoWindow(marker: Marker) {
    Surface(
        elevation = 3.dp,
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.8f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_gintarine_logo),
                contentDescription = "Gintarinė vaistinė"
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = marker.title ?: "",
                style = Typography.subtitle1,
                color = Color.Black
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = marker.snippet ?: "",
                style = Typography.subtitle1,
                color = Color.Black
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = String.format(
                    "%.5f, %.5f",
                    marker.position.latitude, marker.position.longitude
                ),
                style = Typography.subtitle2,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClusterItemInfoWindowPreview() {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Gintarine vaistine",
                    style = Typography.subtitle1
                )
                Text(
                    text = "https://gintarine.lt",
                    style = Typography.subtitle1
                )
                Text(
                    text = "-64.2341234678; 36.123412346",
                    style = Typography.subtitle2
                )
            }
        }
    }
}
