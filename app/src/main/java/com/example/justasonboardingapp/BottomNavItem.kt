package com.example.justasonboardingapp

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val name: String,
    val route: String,
    val iconInactive: ImageVector,
    val iconActive: ImageVector
)