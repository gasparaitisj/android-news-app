package com.telesoftas.justasonboardingapp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.telesoftas.justasonboardingapp.about.AboutScreen
import com.telesoftas.justasonboardingapp.favorite.FavoriteScreen
import com.telesoftas.justasonboardingapp.sourcelist.SourceListScreen
import com.telesoftas.justasonboardingapp.sourcelist.newslist.NewsListScreen
import com.telesoftas.justasonboardingapp.utils.Constants
import timber.log.Timber

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
    val sourceList = stringResource(id = R.string.top_app_bar_title_source_list)
    val favorite = stringResource(id = R.string.top_app_bar_title_favorite)
    val about = stringResource(id = R.string.top_app_bar_title_about)
    val newsList = stringResource(id = R.string.top_app_bar_title_news_list)
    val topBarTitle = remember { mutableStateOf("") }

    bottomNavController.addOnDestinationChangedListener { controller, destination, arguments ->
        Timber.d(destination.route)
        when (destination.route) {
            Constants.Routes.SOURCE_LIST -> {
                topBarTitle.value = sourceList
            }
            Constants.Routes.FAVORITE -> {
                topBarTitle.value = favorite
            }
            Constants.Routes.ABOUT -> {
                topBarTitle.value = about
            }
            Constants.Routes.NEWS_LIST -> {
                topBarTitle.value = arguments?.getString(Constants.NavArgs.NEWS_LIST_TITLE)
                    ?: newsList
            }
        }
    }

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
        topBar = { TopBar(title = topBarTitle.value) },
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
private fun TopBar(title: String) {
    TopAppBar(
        title = { Text(title) },
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
            route = Constants.Routes.NEWS_LIST,
            arguments = listOf(navArgument(Constants.NavArgs.NEWS_LIST_TITLE) {
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
