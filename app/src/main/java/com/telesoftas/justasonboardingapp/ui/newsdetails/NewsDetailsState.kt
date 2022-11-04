package com.telesoftas.justasonboardingapp.ui.newsdetails

import com.telesoftas.justasonboardingapp.ui.map.MapState
import com.telesoftas.justasonboardingapp.ui.map.utils.LocationClusterItem
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.network.Resource

data class NewsDetailsState(
    val article: Resource<Article> = Resource.success(),
    val location: LocationClusterItem = MapState().items[0],
)
