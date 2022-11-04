package com.telesoftas.justasonboardingapp.ui.sourcelist

import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.SortBy

data class SourceListState(
    val sources: Resource<List<Source>> = Resource.success(),
    val sortType: SortBy = SortBy.NONE,
)
