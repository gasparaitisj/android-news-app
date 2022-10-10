package com.telesoftas.justasonboardingapp.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.theme.Typography

@ExperimentalMaterialApi
@Composable
fun AboutScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 16.dp
                    )
                    .verticalScroll(state = scrollState),
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
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorResource(R.color.checkbox),
                            uncheckedColor = colorResource(R.color.checkbox)
                        ),
                        checked = checkedState.value,
                        onCheckedChange = { checkedState.value = it }
                    )
                    Text(
                        text = stringResource(R.string.about_screen_enable_notifications),
                        style = Typography.subtitle2
                    )
                }
            }
        }
    )
}
