package com.telesoftas.justasonboardingapp.favorite

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
import com.telesoftas.justasonboardingapp.BottomNavigationBar
import com.telesoftas.justasonboardingapp.R

@ExperimentalMaterialApi
@Composable
fun FavoriteScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.favorite_screen_favorite))
                },
                backgroundColor = colorResource(id = R.color.topAppBarBackground),
                contentColor = colorResource(id = R.color.topAppBarContent)
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding()
                    ),
                content = {
                    Text(text = "Favorite screen")
                }
            )
        }
    )
}
