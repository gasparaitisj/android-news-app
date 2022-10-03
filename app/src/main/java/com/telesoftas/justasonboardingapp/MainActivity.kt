package com.telesoftas.justasonboardingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.telesoftas.justasonboardingapp.tutorial.TutorialScreen
import com.telesoftas.justasonboardingapp.ui.theme.JustasOnboardingAppTheme
import com.telesoftas.justasonboardingapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

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
                val navController = rememberNavController()
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
                route = Constants.Routes.SOURCE_LIST,
                iconInactiveResId = R.drawable.btn_source_list,
                iconActiveResId = R.drawable.btn_source_list_active
            ),
            BottomNavItem(
                name = stringResource(id = R.string.bottom_navigation_favorite),
                route = Constants.Routes.FAVORITE,
                iconInactiveResId = R.drawable.btn_favorite,
                iconActiveResId = R.drawable.btn_favorite_active
            ),
            BottomNavItem(
                name = stringResource(id = R.string.bottom_navigation_about),
                route = Constants.Routes.ABOUT,
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


@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Constants.Routes.MAIN) {
        composable(Constants.Routes.TUTORIAL) {
            TutorialScreen(navController = navController)
        }
        composable(Constants.Routes.MAIN) {
            MainScreen(navController = navController)
        }
    }
}
