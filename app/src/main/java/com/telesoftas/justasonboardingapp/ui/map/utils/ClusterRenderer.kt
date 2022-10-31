package com.telesoftas.justasonboardingapp.ui.map.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.telesoftas.justasonboardingapp.R

// Customization of both clusters and markers happens here
class ClusterRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<LocationClusterItem>?,
    private val clusterColors: ClusterColors = ClusterColors()
) : DefaultClusterRenderer<LocationClusterItem>(context, map, clusterManager) {
    override fun getColor(clusterSize: Int): Int {
        return when (clusterSize) {
            in 0 .. 9 -> clusterColors.small.toArgb()
            in 10..19 -> clusterColors.medium.toArgb()
            else -> clusterColors.large.toArgb()
        }
    }
    override fun onBeforeClusterItemRendered(item: LocationClusterItem, markerOptions: MarkerOptions) {
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
