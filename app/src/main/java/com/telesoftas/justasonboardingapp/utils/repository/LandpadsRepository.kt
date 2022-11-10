package com.telesoftas.justasonboardingapp.utils.repository

import com.apollographql.apollo3.ApolloClient
import com.telesoftas.justasonboardingapp.LandpadsQuery
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.Landpad
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LandpadsRepository @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun getLandpads(): Resource<List<Landpad>> {
        return try {
            apolloClient.query(LandpadsQuery()).execute().data?.landpads.toResource()
        } catch (exception: Exception) {
            Timber.e(exception.message)
            Resource.error(exception.message)
        }
    }
}
