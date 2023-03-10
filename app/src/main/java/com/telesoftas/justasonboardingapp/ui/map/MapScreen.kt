package com.telesoftas.justasonboardingapp.ui.map

import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.compose.*
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.main.navigation.TopBar
import com.telesoftas.justasonboardingapp.ui.map.utils.ClusterColors
import com.telesoftas.justasonboardingapp.ui.map.utils.ClusterManager
import com.telesoftas.justasonboardingapp.ui.map.utils.LocationClusterItem
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.navigation.Screen
import com.telesoftas.justasonboardingapp.utils.network.Status

@MapsComposeExperimentalApi
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    MapScreenContent(state)
}

@MapsComposeExperimentalApi
@Composable
private fun MapScreenContent(
    state: MapState
) {
    var currentTab by remember { mutableStateOf(0) }
    Scaffold(
        topBar = { TopBar(stringResource(id = Screen.Map.titleResId)) },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            MapScreenTabRow(
                currentTab = currentTab,
                onFirstTabClicked = { currentTab = 0 },
                onSecondTabClicked = { currentTab = 1 }
            )
            when (currentTab) {
                0 -> {
                    GoogleMapWithClustering(
                        items = state.pharmacyLocations,
                        cameraPosition = state.pharmacyCameraPosition,
                        modifier = Modifier.fillMaxSize(),
                        clusterInfoWindowContent = { PharmacyClusterInfoWindow(it) },
                        clusterItemInfoWindowContent = { PharmacyClusterItemInfoWindow(it) }
                    )
                }
                1 -> {
                    val locations = state.landpadLocations.data?.mapNotNull { it.toClusterItem() }
                    if (state.landpadLocations.status == Status.SUCCESS && locations != null) {
                        GoogleMapWithClustering(
                            items = locations,
                            cameraPosition = state.landpadCameraPosition,
                            modifier = Modifier.fillMaxSize(),
                            clusterColors = ClusterColors(
                                small = Color.Red,
                                medium = Color.DarkGray,
                                large = Color.Blue
                            ),
                            clusterInfoWindowContent = { LandpadsClusterInfoWindow(it) },
                            clusterItemInfoWindowContent = { marker ->
                                val markerItem = locations.firstOrNull { it.itemPosition == marker.position } ?:
                                    LocationClusterItem(
                                        marker.position,
                                        marker.title ?: "",
                                        marker.snippet ?: ""
                                    )
                                LandpadsClusterItemInfoWindow(markerItem)
                            }
                        )
                    }
                }
            }
        }
    }
}

@MapsComposeExperimentalApi
@Composable
fun GoogleMapWithClustering(
    items: List<LocationClusterItem>,
    cameraPosition: CameraPosition,
    modifier: Modifier,
    uiSettings: MapUiSettings = MapUiSettings(),
    clusterColors: ClusterColors = ClusterColors(),
    clusterInfoWindowContent: @Composable (Cluster<LocationClusterItem>?) -> Unit,
    clusterItemInfoWindowContent: @Composable (Marker) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    var clusterManager by remember { mutableStateOf<ClusterManager?>(null) }
    val context = LocalContext.current
    val viewGroup = LocalView.current as ViewGroup
    val compositionContext = rememberCompositionContext()
    val cameraPositionState = CameraPositionState(cameraPosition)

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
                clusterColors = clusterColors,
                clusterItemInfoWindowContent = { marker ->
                    clusterItemInfoWindowContent(marker)
                },
                clusterInfoWindowContent = {
                    clusterInfoWindowContent(clusterManager?.clickedCluster)
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
private fun MapScreenTabRow(
    currentTab: Int,
    onFirstTabClicked: () -> Unit,
    onSecondTabClicked: () -> Unit,
) {
    TabRow(selectedTabIndex = currentTab) {
        Tab(
            selected = currentTab == 0,
            onClick = onFirstTabClicked,
            text = {
                Text(text = "Google")
            }
        )
        Tab(
            selected = currentTab == 1,
            onClick = onSecondTabClicked,
            text = {
                Text(text = "SpaceX")
            }
        )
    }
}

@Composable
fun PharmacyClusterInfoWindow(
    cluster: Cluster<LocationClusterItem>?
) {
    val locationText = if (cluster?.size != null) {
        "Gintarin?? vaistin?? (${cluster.size} locations)"
    } else "Gintarin?? vaistin??"

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
                contentDescription = "Gintarin?? vaistin??"
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
fun LandpadsClusterInfoWindow(
    cluster: Cluster<LocationClusterItem>?
) {
    val locationText = if (cluster?.size != null) {
        "SpaceX Landpad (${cluster.size} locations)"
    } else "SpaceX"

    Surface(
        elevation = 3.dp,
        shape = RoundedCornerShape(24.dp),
        color = Color.Blue.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_spacex_logo),
                contentDescription = "SpaceX"
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
fun PharmacyClusterItemInfoWindow(marker: Marker) {
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
                contentDescription = "Gintarin?? vaistin??"
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

@Composable
fun LandpadsClusterItemInfoWindow(item: LocationClusterItem) {
    Surface(
        elevation = 3.dp,
        shape = RoundedCornerShape(24.dp),
        color = Color.Blue.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_spacex_logo),
                contentDescription = "SpaceX"
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = item.title,
                style = Typography.subtitle1,
                color = Color.Black
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = item.snippet,
                style = Typography.subtitle1,
                color = Color.Black
            )
            Text(
                modifier = Modifier.padding(4.dp),
                text = String.format(
                    "%.5f, %.5f",
                    item.position.latitude, item.position.longitude
                ),
                style = Typography.subtitle2,
                color = Color.Black
            )
            if (item.attemptedLandings != null && item.successfulLandings != null) {
                val successRatio = try {
                    item.successfulLandings.toDouble().div(item.attemptedLandings.toDouble()) * 100
                } catch (exception: Exception) { null }
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = "Attempted landings: ${item.attemptedLandings}",
                    style = Typography.subtitle2,
                    color = Color.Black
                )
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = "Successful landings: ${item.successfulLandings}",
                    style = Typography.subtitle2,
                    color = Color.Black
                )
                if (successRatio != null) {
                    Text(
                        modifier = Modifier.padding(4.dp),
                        text = String.format(
                            "Success ratio: %.2f%%",
                            successRatio
                        ),
                        style = Typography.subtitle2,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClusterItemInfoWindowPreview() {
    LandpadsClusterItemInfoWindow(
        LocationClusterItem(
            itemPosition = LatLng(54.685581219627494, 25.204550482478087),
            itemTitle = "Labas",
            itemSnippet = "Vakaras"
        )
    )
}
