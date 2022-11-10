package com.telesoftas.justasonboardingapp.utils.repository

import com.telesoftas.justasonboardingapp.LandpadsQuery
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.Landpad
import com.telesoftas.justasonboardingapp.utils.network.data.Location

fun List<LandpadsQuery.Landpad?>?.toResource(): Resource<List<Landpad>> =
    Resource.success(
        this?.map { landpad ->
            Landpad(
                location = Location(landpad?.location?.latitude ?: 0.0, landpad?.location?.longitude ?: 0.0),
                fullName = landpad?.full_name ?: "",
                successfulLandings = landpad?.successful_landings ?: "",
                attemptedLandings = landpad?.attempted_landings ?: "",
                wikipedia = landpad?.wikipedia ?: ""
            )
        } ?: listOf()
    )
