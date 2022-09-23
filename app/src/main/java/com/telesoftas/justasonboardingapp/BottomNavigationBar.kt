package com.telesoftas.justasonboardingapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.justasonboardingapp.R

@ExperimentalMaterialApi
@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onItemClick: (BottomNavItem) -> Unit
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    BottomNavigation(
        modifier = modifier,
        backgroundColor = colorResource(id = R.color.botNavBar)
    ) {
        items.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                selectedContentColor = colorResource(id = R.color.selectedContent),
                unselectedContentColor = colorResource(id = R.color.unselectedContent),
                alwaysShowLabel = false,
                icon = {
                    Column(horizontalAlignment = CenterHorizontally) {
                        Icon(
                            painter = if (selected) {
                                painterResource(id = item.iconActiveResId)
                            } else {
                                painterResource(id = item.iconInactiveResId)
                            },
                            contentDescription = item.name
                        )
                    }
                },
                label = { Text(text = item.name) }
            )
        }
    }
}
