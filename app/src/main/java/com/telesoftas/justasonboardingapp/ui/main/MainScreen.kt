package com.telesoftas.justasonboardingapp.ui.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.telesoftas.justasonboardingapp.ui.main.navigation.BottomNavigationBarNavigation
import com.telesoftas.justasonboardingapp.utils.navigation.Screen

@ExperimentalPermissionsApi
@MapsComposeExperimentalApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalLifecycleComposeApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val bottomNavController = rememberAnimatedNavController()
    handleFirstLaunch(viewModel, navController)

    MainScreenContent(bottomNavController)
}

@ExperimentalPermissionsApi
@MapsComposeExperimentalApi
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@ExperimentalLifecycleComposeApi
@ExperimentalPagerApi
@ExperimentalAnimationApi
@Composable
private fun MainScreenContent(bottomNavController: NavHostController) {
    Scaffold(
        bottomBar = { MainBottomNavigationBar(navController = bottomNavController) },
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
