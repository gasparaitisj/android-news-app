package com.example.justasonboardingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.justasonboardingapp.about.AboutScreen
import com.example.justasonboardingapp.favorite.FavoriteScreen
import com.example.justasonboardingapp.sourcelist.SourceListScreen
import com.example.justasonboardingapp.ui.theme.JustasOnboardingAppTheme

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {
            JustasOnboardingAppTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(
                            items = listOf(
                                BottomNavItem(
                                    name = "Source list",
                                    route = "source-list",
                                    iconInactiveResId = R.drawable.btn_source_list,
                                    iconActiveResId = R.drawable.btn_source_list_active
                                ),
                                BottomNavItem(
                                    name = "Favorite",
                                    route = "favorite",
                                    iconInactiveResId = R.drawable.btn_favorite,
                                    iconActiveResId = R.drawable.btn_favorite_active
                                ),
                                BottomNavItem(
                                    name = "About",
                                    route = "about",
                                    iconInactiveResId = R.drawable.btn_about,
                                    iconActiveResId = R.drawable.btn_about_active
                                ),
                            ),
                            navController = navController,
                            onItemClick = {
                                navController.navigate(it.route)
                            }
                        )
                    }
                ) {
                    it.calculateBottomPadding()
                    Navigation(navController = navController)
                }
            }
        }
    }
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "source-list") {
        composable("source-list") {
            SourceListScreen()
        }
        composable("favorite") {
            FavoriteScreen()
        }
        composable("about") {
            AboutScreen()
        }
    }
}

