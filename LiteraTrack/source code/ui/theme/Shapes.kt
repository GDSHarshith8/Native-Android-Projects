package com.literatrack.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp), // For very small elements
    small = RoundedCornerShape(14.dp), // For chips, small buttons
    medium = RoundedCornerShape(16.dp), // For cards, dialogs
    large = RoundedCornerShape(20.dp), // For larger cards, sheets
    extraLarge = RoundedCornerShape(28.dp) // For full-screen elements or very prominent containers
)