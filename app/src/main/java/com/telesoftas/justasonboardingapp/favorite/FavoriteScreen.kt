package com.telesoftas.justasonboardingapp.favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.telesoftas.justasonboardingapp.BottomNavigationBar

@ExperimentalMaterialApi
@Composable
fun FavoriteScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                content = {
                    Text(text = "Favorite screen")
                }
            )
        }
    )
}
