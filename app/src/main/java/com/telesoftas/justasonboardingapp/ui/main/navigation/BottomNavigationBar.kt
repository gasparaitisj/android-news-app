package com.telesoftas.justasonboardingapp.ui.main.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.about.AboutScreen
import com.telesoftas.justasonboardingapp.ui.favorite.FavoriteScreen
import com.telesoftas.justasonboardingapp.ui.map.MapScreen
import com.telesoftas.justasonboardingapp.ui.newsdetails.NewsDetailsScreen
import com.telesoftas.justasonboardingapp.ui.sourcelist.SourceListScreen
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.NewsListScreen
import com.telesoftas.justasonboardingapp.utils.navigation.Screen

@ExperimentalMaterialApi
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier,
        backgroundColor = colorResource(id = R.color.bottom_navigation_bar)
    ) {
        items.forEach { item ->
            val selected = handleSelectedBottomNavigationItem(
                currentRoute = backStackEntry.value?.destination?.route,
                itemRoute = item.route
            )
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                selectedContentColor = colorResource(id = R.color.selected_content),
                unselectedContentColor = colorResource(id = R.color.unselected_content),
                alwaysShowLabel = false,
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        Icon(
                            painter = if (selected) {
                                painterResource(id = item.iconActiveResId)
                            } else {
                                painterResource(id = item.iconInactiveResId)
                            },
                            contentDescription = item.name
                        )
                    }
                },
                label = { Text(text = item.name) }
            )
        }
    }
}

// If screen is child of any bottom navigation screen, the parent item is selected
private fun handleSelectedBottomNavigationItem(
    currentRoute: String?,
    itemRoute: String
): Boolean = when (itemRoute) {
    Screen.SourceList.route ->
        currentRoute == Screen.SourceList.route ||
        currentRoute == Screen.NewsList.route ||
        currentRoute == Screen.NewsDetails.route
    Screen.Favorite.route ->
        currentRoute == Screen.Favorite.route ||
        currentRoute == Screen.FavoriteNewsDetails.route
    Screen.Map.route -> currentRoute == Screen.Map.route
    Screen.About.route -> currentRoute == Screen.About.route
    else -> false
}

@ExperimentalPermissionsApi
@MapsComposeExperimentalApi
@ExperimentalComposeUiApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun BottomNavigationBarNavigation(navController: NavHostController) {
    AnimatedNavHost(navController = navController, startDestination = Screen.SourceList.route) {
        composable(Screen.SourceList.route) {
            SourceListScreen(navController = navController)
        }
        composable(
            route = Screen.NewsList.route,
            arguments = listOf(navArgument(Screen.NewsList.KEY_TITLE) { type = NavType.StringType })
        ) {
            NewsListScreen(navController = navController)
        }
        composable(
            route = Screen.NewsDetails.route,
            arguments = listOf(navArgument(Screen.NewsDetails.KEY_ID) { type = NavType.StringType }),
            enterTransition = { scaleIn(animationSpec = tween(500)) },
            exitTransition = { scaleOut(animationSpec = tween(500)) },
        ) {
            NewsDetailsScreen(navController = navController)
        }
        composable(
            route = Screen.FavoriteNewsDetails.route,
            arguments = listOf(navArgument(Screen.FavoriteNewsDetails.KEY_ID) { type = NavType.StringType }),
            enterTransition = { scaleIn(animationSpec = tween(500)) },
            exitTransition = { scaleOut(animationSpec = tween(500)) },
        ) {
            NewsDetailsScreen(navController = navController)
        }
        composable(Screen.Favorite.route) {
            FavoriteScreen(navController = navController)
        }
        composable(Screen.Map.route) {
            MapScreen()
        }
        composable(Screen.About.route) {
            AboutScreen()
        }
    }
}

data class BottomNavItem(
    val name: String,
    val route: String,
    @DrawableRes val iconInactiveResId: Int,
    @DrawableRes val iconActiveResId: Int
)
