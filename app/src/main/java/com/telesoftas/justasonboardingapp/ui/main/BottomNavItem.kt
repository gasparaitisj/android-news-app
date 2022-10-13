package com.telesoftas.justasonboardingapp.ui.main

import androidx.annotation.DrawableRes

data class BottomNavItem(
    val name: String,
    val route: String,
    @DrawableRes val iconInactiveResId: Int,
    @DrawableRes val iconActiveResId: Int
)
