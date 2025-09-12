package com.taskchecker.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp), // For very small elements
    small = RoundedCornerShape(10.dp), // For chips, small buttons
    medium = RoundedCornerShape(12.dp), // For cards, dialogs
    large = RoundedCornerShape(16.dp), // For larger cards, sheets
    extraLarge = RoundedCornerShape(24.dp) // For full-screen elements or very prominent containers
)