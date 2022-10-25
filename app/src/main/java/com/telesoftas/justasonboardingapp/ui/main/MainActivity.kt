package com.telesoftas.justasonboardingapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.theme.JustasOnboardingAppTheme
import com.telesoftas.justasonboardingapp.ui.tutorial.TutorialScreen
import com.telesoftas.justasonboardingapp.utils.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPermissionsApi
@MapsComposeExperimentalApi
@ExperimentalComposeUiApi
@ExperimentalLifecycleComposeApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {
            JustasOnboardingAppTheme {
                val navController = rememberAnimatedNavController()
                Navigation(navController = navController)
            }
        }
    }
}

@Composable
@ExperimentalMaterialApi
fun BottomNavigationBar(navController: NavHostController) {
    BottomNavigationBar(
        items = listOf(
            BottomNavItem(
                name = stringResource(id = R.string.bottom_navigation_source_list),
                route = Screen.SourceList.route,
                iconInactiveResId = R.drawable.btn_source_list,
                iconActiveResId = R.drawable.btn_source_list_active
            ),
            BottomNavItem(
                name = stringResource(id = R.string.bottom_navigation_favorite),
                route = Screen.Favorite.route,
                iconInactiveResId = R.drawable.btn_favorite,
                iconActiveResId = R.drawable.btn_favorite_active
            ),
            BottomNavItem(
                name = stringResource(id = R.string.bottom_navigation_map),
                route = Screen.Map.route,
                iconInactiveResId = R.drawable.btn_map,
                iconActiveResId = R.drawable.btn_map_active
            ),
            BottomNavItem(
                name = stringResource(id = R.string.bottom_navigation_about),
                route = Screen.About.route,
                iconInactiveResId = R.drawable.btn_about,
                iconActiveResId = R.drawable.btn_about_active
            ),
        ),
        navController = navController,
        onItemClick = {
            navController.navigate(it.route) {
                navController.graph.startDestinationRoute?.let { screen_route ->
                    popUpTo(screen_route) {
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}


@ExperimentalPermissionsApi
@MapsComposeExperimentalApi
@ExperimentalComposeUiApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun Navigation(navController: NavHostController) {
    AnimatedNavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Tutorial.route) {
            TutorialScreen(navController = navController)
        }
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
    }
}
