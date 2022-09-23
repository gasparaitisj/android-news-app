package com.telesoftas.justasonboardingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.telesoftas.justasonboardingapp.about.AboutScreen
import com.telesoftas.justasonboardingapp.favorite.FavoriteScreen
import com.telesoftas.justasonboardingapp.sourcelist.SourceListScreen
import com.telesoftas.justasonboardingapp.ui.theme.JustasOnboardingAppTheme

@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {
            JustasOnboardingAppTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(bottom = paddingValues.calculateBottomPadding())
                    ) {
                        Navigation(navController = navController)
                    }
                }
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
                route = Routes.SOURCE_LIST,
                iconInactiveResId = R.drawable.btn_source_list,
                iconActiveResId = R.drawable.btn_source_list_active
            ),
            BottomNavItem(
                name = stringResource(id = R.string.bottom_navigation_favorite),
                route = Routes.FAVORITE,
                iconInactiveResId = R.drawable.btn_favorite,
                iconActiveResId = R.drawable.btn_favorite_active
            ),
            BottomNavItem(
                name = stringResource(id = R.string.bottom_navigation_about),
                route = Routes.ABOUT,
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


@Composable
private fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.SOURCE_LIST) {
        composable(Routes.SOURCE_LIST) {
            SourceListScreen()
        }
        composable(Routes.FAVORITE) {
            FavoriteScreen()
        }
        composable(Routes.ABOUT) {
            AboutScreen()
        }
    }
}

object Routes {
    const val SOURCE_LIST = "source-list"
    const val FAVORITE = "favorite"
    const val ABOUT = "about"
}
