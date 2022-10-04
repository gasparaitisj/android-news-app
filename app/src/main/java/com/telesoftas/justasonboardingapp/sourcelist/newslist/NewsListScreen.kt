package com.telesoftas.justasonboardingapp.sourcelist.newslist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@ExperimentalMaterialApi
@Composable
fun NewsListScreen(
    navController: NavHostController,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                content = {
                    Text(text = viewModel.sourceTitle)
                }
            )
        }
    )
}
