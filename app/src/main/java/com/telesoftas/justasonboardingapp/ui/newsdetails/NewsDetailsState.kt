package com.telesoftas.justasonboardingapp.ui.newsdetails

import com.telesoftas.justasonboardingapp.ui.map.MapState
import com.telesoftas.justasonboardingapp.ui.map.utils.LocationClusterItem
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.ArticleViewData
import com.telesoftas.justasonboardingapp.utils.network.Resource

data class NewsDetailsState(
    val article: Resource<ArticleViewData> = Resource.success(),
    val location: LocationClusterItem = MapState().items[0],
)
