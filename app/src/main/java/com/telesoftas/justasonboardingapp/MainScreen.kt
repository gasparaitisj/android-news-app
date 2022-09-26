package com.telesoftas.justasonboardingapp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.telesoftas.justasonboardingapp.about.AboutScreen
import com.telesoftas.justasonboardingapp.favorite.FavoriteScreen
import com.telesoftas.justasonboardingapp.sourcelist.SourceListScreen

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MainScreen() {
    val bottomNavController = rememberNavController()
    Scaffold(
        topBar = { TopBar(navController = bottomNavController) },
        bottomBar = { BottomNavigationBar(navController = bottomNavController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                content = {
                    BottomNavigationBarNavigation(navController = bottomNavController)
                }
            )
        }
    )
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun TopBar(navController: NavHostController) {
    TopAppBar(
        title = {
            when (navController.currentBackStackEntryAsState().value?.destination?.route) {
                Routes.SOURCE_LIST -> {
                    Text(stringResource(id = R.string.source_list_screen_source_list))
                }
                Routes.FAVORITE -> {
                    Text(stringResource(id = R.string.favorite_screen_favorite))
                }
                Routes.ABOUT -> {
                    Text(stringResource(id = R.string.about_screen_about))
                }
            }
        },
        backgroundColor = colorResource(id = R.color.topAppBarBackground),
        contentColor = colorResource(id = R.color.topAppBarContent)
    )
}


@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun BottomNavigationBarNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.SOURCE_LIST) {
        composable(Routes.SOURCE_LIST) {
            SourceListScreen(navController = navController)
        }
        composable(Routes.FAVORITE) {
            FavoriteScreen(navController = navController)
        }
        composable(Routes.ABOUT) {
            AboutScreen(navController = navController)
        }
    }
}
