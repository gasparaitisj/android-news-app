package com.telesoftas.justasonboardingapp.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.telesoftas.justasonboardingapp.R

val Roboto = FontFamily(
    Font(R.font.roboto_medium, FontWeight.W500),
    Font(R.font.roboto_regular, FontWeight.W400)
)

val Typography = Typography(
    defaultFontFamily = Roboto,
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        color = DarkBlue
    )
)
