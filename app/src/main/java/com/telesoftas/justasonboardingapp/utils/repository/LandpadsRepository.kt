package com.telesoftas.justasonboardingapp.utils.repository

import com.apollographql.apollo3.ApolloClient
import com.telesoftas.justasonboardingapp.LandpadsQuery
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.Landpad
import com.telesoftas.justasonboardingapp.utils.network.data.Location
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LandpadsRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun getLandpads(): Resource<List<Landpad>> {
        return try {
            Resource.success(
                apolloClient.query(LandpadsQuery()).execute().data?.landpads?.map { landpad ->
                    Landpad(
                        location = Location(landpad?.location?.latitude, landpad?.location?.longitude),
                        fullName = landpad?.full_name,
                        successfulLandings = landpad?.successful_landings,
                        attemptedLandings = landpad?.attempted_landings,
                        wikipedia = landpad?.wikipedia
                    )
                } ?: listOf()
            )
        } catch (exception: Exception) {
            Timber.e(exception.message)
            Resource.error(exception.message)
        }
    }
}
