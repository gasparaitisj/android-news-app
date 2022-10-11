package com.telesoftas.justasonboardingapp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.telesoftas.justasonboardingapp.about.AboutScreen
import com.telesoftas.justasonboardingapp.favorite.FavoriteScreen
import com.telesoftas.justasonboardingapp.newsdetails.NewsDetailsScreen
import com.telesoftas.justasonboardingapp.sourcelist.SourceListScreen
import com.telesoftas.justasonboardingapp.sourcelist.newslist.NewsListScreen
import com.telesoftas.justasonboardingapp.utils.Screen
import timber.log.Timber

@ExperimentalMaterialApi
@ExperimentalLifecycleComposeApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val bottomNavController = rememberNavController()
    val topBarTitle: MutableState<String> = remember { mutableStateOf("") }
    val topBarRoute: MutableState<String?> = remember { mutableStateOf("") }
    setOnDestinationChangedListener(bottomNavController, topBarTitle, topBarRoute)
    handleFirstLaunch(viewModel, navController)

    MainScreenContent(topBarTitle, topBarRoute, bottomNavController)
}

@ExperimentalMaterialApi
@ExperimentalLifecycleComposeApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
private fun MainScreenContent(
    topBarTitle: MutableState<String>,
    topBarRoute: MutableState<String?>,
    bottomNavController: NavHostController
) {
    Scaffold(
        topBar = {
            when (topBarRoute.value) {
                Screen.NewsList.route -> {
                    TopBarNewsList(topBarTitle.value, bottomNavController)
                }
                Screen.NewsDetails.route -> {
                    // Collapsing Toolbar is implemented in NewsDetailsScreen.kt
                }
                else -> TopBar(topBarTitle.value)
            }
        },
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

@Composable
private fun handleFirstLaunch(
    viewModel: MainViewModel,
    navController: NavHostController
) {
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState(initial = false)

    if (isFirstLaunch) {
        LaunchedEffect(true) {
            viewModel.setFirstLaunchCompleted()
            navController.navigate(route = Screen.Tutorial.route) {
                popUpTo(Screen.Main.route) { inclusive = true }
            }
        }
    }
}

@Composable
private fun setOnDestinationChangedListener(
    bottomNavController: NavHostController,
    topBarTitle: MutableState<String>,
    topBarRoute: MutableState<String?>
) {
    val sourceList = stringResource(id = R.string.top_app_bar_title_source_list)
    val favorite = stringResource(id = R.string.top_app_bar_title_favorite)
    val about = stringResource(id = R.string.top_app_bar_title_about)
    val newsList = stringResource(id = R.string.top_app_bar_title_news_list)
    val newsDetails = stringResource(id = R.string.top_app_bar_title_news_details)

    DisposableEffect(bottomNavController) {
        val callback = NavController.OnDestinationChangedListener { _, destination, arguments ->
            topBarRoute.value = destination.route
            when (destination.route) {
                Screen.SourceList.route -> {
                    topBarTitle.value = sourceList
                }
                Screen.Favorite.route -> {
                    topBarTitle.value = favorite
                }
                Screen.About.route -> {
                    topBarTitle.value = about
                }
                Screen.NewsList.route -> {
                    topBarTitle.value = arguments?.getString(Screen.NewsList.KEY_TITLE)
                        ?: newsList
                }
                Screen.NewsDetails.route -> {
                    topBarTitle.value = arguments?.getString(Screen.NewsList.KEY_TITLE)
                        ?: newsDetails
                }
            }
        }
        bottomNavController.addOnDestinationChangedListener(callback)
        onDispose {
            Timber.d("disposed!")
            bottomNavController.removeOnDestinationChangedListener(callback)
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun TopBar(title: String) {
    TopAppBar(
        title = { Text(title) },
        backgroundColor = colorResource(id = R.color.top_app_bar_background),
        contentColor = colorResource(id = R.color.top_app_bar_content)
    )
}

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun TopBarNewsList(
    title: String,
    navController: NavHostController
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        backgroundColor = colorResource(id = R.color.top_app_bar_background),
        contentColor = colorResource(id = R.color.top_app_bar_content)
    )
}

@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun BottomNavigationBarNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.SourceList.route) {
        composable(Screen.SourceList.route) {
            SourceListScreen(navController = navController)
        }
        composable(
            route = Screen.NewsList.route,
            arguments = listOf(navArgument(Screen.NewsList.KEY_TITLE) {
                type = NavType.StringType
            })
        ) {
            NewsListScreen(navController = navController)
        }
        composable(
            route = Screen.NewsDetails.route,
            arguments = listOf(navArgument(Screen.NewsDetails.KEY_ID) {
                type = NavType.StringType
            })
        ) {
            NewsDetailsScreen(navController = navController)
        }
        composable(Screen.Favorite.route) {
            FavoriteScreen(navController = navController)
        }
        composable(Screen.About.route) {
            AboutScreen(navController = navController)
        }
    }
}
