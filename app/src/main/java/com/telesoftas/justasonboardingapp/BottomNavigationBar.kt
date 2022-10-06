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
import com.telesoftas.justasonboardingapp.utils.Screen

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
        backgroundColor = colorResource(id = R.color.bottom_navigation_bar)
    ) {
        items.forEach { item ->
            val selected = handleSelectedBottomNavigationItem(
                currentRoute = backStackEntry.value?.destination?.route,
                itemRoute = item.route
            )
            BottomNavigationItem(
                selected = selected,
                onClick = { onItemClick(item) },
                selectedContentColor = colorResource(id = R.color.selected_content),
                unselectedContentColor = colorResource(id = R.color.unselected_content),
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

// If screen is child of any bottom navigation screen, the parent item is selected
private fun handleSelectedBottomNavigationItem(
    currentRoute: String?,
    itemRoute: String
): Boolean = when (itemRoute) {
    Screen.SourceList.route ->
        currentRoute == Screen.SourceList.route || currentRoute == Screen.NewsList.route
    Screen.Favorite.route -> currentRoute == Screen.Favorite.route
    Screen.About.route -> currentRoute == Screen.About.route
    else -> false
}
