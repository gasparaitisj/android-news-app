package com.telesoftas.justasonboardingapp.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.theme.Typography

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
                    .padding(bottom = paddingValues.calculateTopPadding())
            ) {
                Text(
                    text = stringResource(id = R.string.about_screen_paragraph_1),
                    style = Typography.subtitle2
                )
                Text(
                    text = stringResource(id = R.string.about_screen_paragraph_2),
                    style = Typography.body2
                )
                Text(
                    text = stringResource(id = R.string.about_screen_paragraph_3),
                    style = Typography.body2
                )
                Text(
                    text = stringResource(id = R.string.about_screen_paragraph_4),
                    style = Typography.subtitle2
                )
                Text(
                    text = stringResource(id = R.string.about_screen_paragraph_5),
                    style = Typography.body2
                )
            }
        }
    )
}

@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}
