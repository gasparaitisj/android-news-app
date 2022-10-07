package com.telesoftas.justasonboardingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.telesoftas.justasonboardingapp.utils.Screen
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterial3Api
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


@ExperimentalMaterial3Api
@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Tutorial.route) {
            TutorialScreen(navController = navController)
        }
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
    }
}
