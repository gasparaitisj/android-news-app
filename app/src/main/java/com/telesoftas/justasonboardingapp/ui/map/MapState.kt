package com.telesoftas.justasonboardingapp.ui.map

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.telesoftas.justasonboardingapp.ui.map.utils.LocationClusterItem
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.Landpad

data class MapState(
    val pharmacyLocations: List<LocationClusterItem> = listOf(
        LatLng(54.685581219627494, 25.204550482478087),
        LatLng(54.67993210823842, 25.213782567975255),
        LatLng(54.673290870679786, 25.239226770855613),
        LatLng(54.682369758321926, 25.265834137296213),
        LatLng(54.688421222269014, 25.282409218029706),
        LatLng(54.68455511326609, 25.27499405033314),
        LatLng(54.72002558858096, 25.230111379737366),
        LatLng(54.720229196167324, 25.222444314023623),
        LatLng(54.71858162659916, 25.229246508945455),
        LatLng(54.71693399006972, 25.245703432143443),
        LatLng(54.715413035377495, 25.239998365434808),
        LatLng(54.65668595118835, 25.223322015034977),
        LatLng(54.93156706638847, 23.986814190846722),
        LatLng(54.92672892780735, 23.98260410424033),
        LatLng(54.92531769443727, 23.95892236707938),
        LatLng(54.92793851700808, 23.947870889737608),
        LatLng(54.918159963017395, 23.961027410382577),
        LatLng(54.910799277666165, 23.965412917264235),
        LatLng(54.94527196575677, 23.88033408376009),
        LatLng(54.935476051597824, 23.886734837277555),
        LatLng(54.929952996463264, 23.887764805518),
        LatLng(54.946322701922725, 23.82836997031912),
        LatLng(54.88534627892093, 23.89480292219648),
        LatLng(54.86440751321727, 23.887593144956625),
        LatLng(54.94755962018759, 23.82744049528682),
        LatLng(55.916997354583486, 23.358224186786803),
        LatLng(55.95247801982904, 23.3266841750896),
        LatLng(55.918470227588195, 23.279111324112982),
        LatLng(55.91655548176986, 23.27254048834273),
        LatLng(55.952330864281926, 23.293304329376728),
        LatLng(55.95218370817552, 23.32589567479717),
        LatLng(55.91788108510149, 23.357698519925183)
    ).map { latLng ->
        LocationClusterItem(
            itemPosition = latLng,
            itemTitle = "Gintarinė vaistinė",
            itemSnippet = "https://gintarine.lt"
        )
    },
    val pharmacyCameraPosition: CameraPosition = CameraPosition.fromLatLngZoom(
        LatLng(54.897790620137464, 23.913658590073002), 6f
    ),
    val landpadLocations: Resource<List<Landpad>> = Resource.success(),
    val landpadCameraPosition: CameraPosition = CameraPosition.fromLatLngZoom(
        LatLng(39.23737824783825, -100.26510980673753), 3f
    ),
)
