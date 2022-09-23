package com.telesoftas.justasonboardingapp.tutorial

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.telesoftas.justasonboardingapp.R

sealed class OnBoardingPage(
    @DrawableRes val image: Int,
    @StringRes val titleResId: Int,
    @StringRes val descriptionResId: Int
) {
    object First : OnBoardingPage(
        image = R.drawable.tutorial_first_image,
        titleResId = R.string.tutorial_screen_first_title,
        descriptionResId = R.string.tutorial_screen_first_description
    )

    object Second : OnBoardingPage(
        image = R.drawable.tutorial_second_image,
        titleResId = R.string.tutorial_screen_second_title,
        descriptionResId = R.string.tutorial_screen_second_description
    )

    object Third : OnBoardingPage(
        image = R.drawable.tutorial_third_image,
        titleResId = R.string.tutorial_screen_third_title,
        descriptionResId = R.string.tutorial_screen_third_description
    )
}
