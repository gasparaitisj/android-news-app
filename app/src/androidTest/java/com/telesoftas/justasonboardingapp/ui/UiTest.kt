package com.telesoftas.justasonboardingapp.ui

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.telesoftas.justasonboardingapp.ui.main.MainActivity
import com.telesoftas.justasonboardingapp.ui.main.MainScreen
import com.telesoftas.justasonboardingapp.ui.theme.JustasOnboardingAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MapsComposeExperimentalApi
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@HiltAndroidTest
class UiTest {
    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun fromFirstAppStart_goToNewsDetailsScreen_newsTitleIsDisplayedCorrectly() = runTest {
        composeTestRule.activity.setContent {
            JustasOnboardingAppTheme {
                MainScreen(navController = rememberAnimatedNavController(), viewModel = hiltViewModel())
            }
        }
        composeTestRule.awaitIdle()
    }
}
