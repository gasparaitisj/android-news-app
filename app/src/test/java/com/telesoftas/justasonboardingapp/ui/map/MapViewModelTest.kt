package com.telesoftas.justasonboardingapp.ui.map

import com.telesoftas.justasonboardingapp.LandpadsQuery
import com.telesoftas.justasonboardingapp.MainCoroutineRule
import com.telesoftas.justasonboardingapp.utils.network.data.Landpad
import com.telesoftas.justasonboardingapp.utils.network.data.Location
import com.telesoftas.justasonboardingapp.utils.repository.LandpadsRepository
import com.telesoftas.justasonboardingapp.utils.repository.toResource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MapViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MapViewModel

    private val landpadsRepository: LandpadsRepository = mockk()

    private val landpads = listOf(
        LandpadsQuery.Landpad(
            location = LandpadsQuery.Location(54.685581219627494, 25.204550482478087),
            full_name = "A Labas",
            successful_landings = "3",
            attempted_landings = "4",
            wikipedia = "https://wikipedia.org"
        ),
        LandpadsQuery.Landpad(
            location = LandpadsQuery.Location(54.685581219627494, 25.204550482478087),
            full_name = "B Labas",
            successful_landings = "",
            attempted_landings = "4",
            wikipedia = "https://wikipedia.org"
        ),
        LandpadsQuery.Landpad(null, null, null, null, null),
        LandpadsQuery.Landpad(
            location = LandpadsQuery.Location(54.685581219627494, 25.204550482478087),
            full_name = "C Labas",
            successful_landings = "3",
            attempted_landings = "",
            wikipedia = "https://wikipedia.org"
        ),
    )

    @Before
    fun setUp() {
        coEvery {
            landpadsRepository.getLandpads()
        } returns landpads.toResource()
    }

    @Test
    fun onViewModelInitialized_stateIsUpdatedCorrectly() = runTest {
        viewModel = MapViewModel(landpadsRepository)
        advanceUntilIdle()
        val answer = listOf(
            Landpad(
                location = Location(54.685581219627494, 25.204550482478087),
                fullName = "A Labas",
                successfulLandings = "3",
                attemptedLandings = "4",
                wikipedia = "https://wikipedia.org"
            ),
            Landpad(
                location = Location(54.685581219627494, 25.204550482478087),
                fullName = "B Labas",
                successfulLandings = "",
                attemptedLandings = "4",
                wikipedia = "https://wikipedia.org"
            ),
            Landpad(
                Location(0.0, 0.0),
                "",
                "",
                "",
                ""
            ),
            Landpad(
                location = Location(54.685581219627494, 25.204550482478087),
                fullName = "C Labas",
                successfulLandings = "3",
                attemptedLandings = "",
                wikipedia = "https://wikipedia.org"
            )
        )

        assertEquals(answer, viewModel.state.value.landpadLocations.data)
    }
}
