package com.telesoftas.justasonboardingapp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.telesoftas.justasonboardingapp.about.AboutScreen
import com.telesoftas.justasonboardingapp.favorite.FavoriteScreen
import com.telesoftas.justasonboardingapp.sourcelist.SourceListScreen
import com.telesoftas.justasonboardingapp.sourcelist.newslist.NewsListScreen
import com.telesoftas.justasonboardingapp.utils.Constants

@ExperimentalLifecycleComposeApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val bottomNavController = rememberNavController()
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsStateWithLifecycle(initialValue = null)

    if (isFirstLaunch == true) {
        LaunchedEffect(isFirstLaunch) {
            viewModel.updateIsFirstLaunch(false)
            navController.navigate(route = Constants.Routes.TUTORIAL) {
                popUpTo(Constants.Routes.MAIN) { inclusive = true }
            }
        }
    }
    Scaffold(
        topBar = { TopBar(navController = bottomNavController) },
        bottomBar = { BottomNavigationBar(navController = bottomNavController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
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
            val backStackEntry = navController.currentBackStackEntryAsState().value
            when (backStackEntry?.destination?.route) {
                Constants.Routes.SOURCE_LIST -> {
                    Text(stringResource(id = R.string.top_app_bar_title_source_list))
                }
                Constants.Routes.FAVORITE -> {
                    Text(stringResource(id = R.string.top_app_bar_title_favorite))
                }
                Constants.Routes.ABOUT -> {
                    Text(stringResource(id = R.string.top_app_bar_title_about))
                }
            }
        },
        backgroundColor = colorResource(id = R.color.topAppBarBackground),
        contentColor = colorResource(id = R.color.topAppBarContent)
    )
}


@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun BottomNavigationBarNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Constants.Routes.SOURCE_LIST) {
        composable(Constants.Routes.SOURCE_LIST) {
            SourceListScreen(navController = navController)
        }

        composable(
            route = "${Constants.Routes.NEWS_LIST}/{${Constants.Routes.NewsListArguments.title}}",
            arguments = listOf(navArgument(Constants.Routes.NewsListArguments.title) {
                type = NavType.StringType
            })
        ) {
            NewsListScreen(navController = navController)
        }
        composable(Constants.Routes.FAVORITE) {
            FavoriteScreen(navController = navController)
        }
        composable(Constants.Routes.ABOUT) {
            AboutScreen(navController = navController)
        }
    }
}
