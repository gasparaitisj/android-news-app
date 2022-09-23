package com.telesoftas.justasonboardingapp.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.telesoftas.justasonboardingapp.R

@Composable
fun AboutScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("About")
                },
                backgroundColor = colorResource(id = R.color.topAppBarBackground),
                contentColor = colorResource(id = R.color.topAppBarContent)
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {}
        }
    )
}

@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}
