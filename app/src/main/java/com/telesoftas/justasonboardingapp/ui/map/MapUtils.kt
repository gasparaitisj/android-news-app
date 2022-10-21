package com.telesoftas.justasonboardingapp.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.theme.DarkBlue
import com.telesoftas.justasonboardingapp.ui.theme.LightPrimary
import com.telesoftas.justasonboardingapp.ui.theme.LightSecondary

class LocationClusterManager(
    context: Context,
    viewGroup: ViewGroup,
    compositionContext: CompositionContext,
    map: GoogleMap,
    items: List<ClusterItem>,
    infoWindowContent: @Composable (Marker) -> Unit
) : ClusterManager<ClusterItem>(context, map) {
    init {
        addItems(items)
        renderer = LocationRenderer(context, map, this)
        markerCollection.setInfoWindowAdapter(
            LocationInfoWindowAdapter(
                viewGroup = viewGroup,
                compositionContext = compositionContext,
                content = infoWindowContent
            )
        )
    }
}

class LocationInfoWindowAdapter(
    private val viewGroup: ViewGroup,
    private val compositionContext: CompositionContext,
    private val content: @Composable (Marker) -> Unit
) : GoogleMap.InfoWindowAdapter {
    private val infoWindowView: ComposeView
        get() = ComposeView(viewGroup.context).apply {
            viewGroup.addView(this)
        }

    override fun getInfoContents(marker: Marker): View? = null

    override fun getInfoWindow(marker: Marker): View {
        return infoWindowView.applyAndRemove(compositionContext) {
            content(marker)
        }
    }

    private fun ComposeView.applyAndRemove(
        parentContext: CompositionContext,
        content: @Composable () -> Unit
    ): ComposeView {
        val result = this.apply {
            setParentCompositionContext(parentContext)
            setContent(content)
        }
        (this.parent as? MapView)?.removeView(this)
        return result
    }
}

class LocationRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<ClusterItem>?,
    private val clusterColors: ClusterColors = ClusterColors()
) : DefaultClusterRenderer<ClusterItem>(context, map, clusterManager) {
    override fun getColor(clusterSize: Int): Int {
        return when (clusterSize) {
            in 0 .. 9 -> clusterColors.small.toArgb()
            in 10..19 -> clusterColors.medium.toArgb()
            else -> clusterColors.large.toArgb()
        }
    }
    override fun onBeforeClusterItemRendered(item: ClusterItem, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)
        markerOptions.icon(getBitmapDescriptorFromVector(context, R.drawable.img_marker))
    }

    private fun getBitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

class ClusterColors(
    val small: Color = LightSecondary,
    val medium: Color = DarkBlue,
    val large: Color = LightPrimary,
)
