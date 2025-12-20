package com.profileviewer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontLoadingStrategy
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.profileviewer.R

val GoogleSans = FontFamily(
    Font(
        R.font.google_sans_regular,
        weight = FontWeight.Normal,
        style = FontStyle.Normal,
        loadingStrategy = FontLoadingStrategy.Async
    )
)

val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = GoogleSans,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
