package com.telesoftas.justasonboardingapp.about

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    .padding(
                        top = paddingValues.calculateTopPadding()
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 16.dp
                    ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
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

                val checkedState = remember { mutableStateOf(true) }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = checkedState.value,
                        onCheckedChange = { checkedState.value = it }
                    )
                    Text(text = stringResource(R.string.about_screen_enable_notifications))
                }
                
            }
        }
    )
}

@Preview
@Composable
fun AboutScreenPreview() {
    AboutScreen()
}