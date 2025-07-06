package com.composetrails.projects.until6.projectScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation

@Composable
fun EmptyTaskSurface(
    message: String,
    padding: PaddingValues = PaddingValues(0.dp) // Accepts scaffold padding
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding), // ✅ Scaffold padding applied here
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .height(240.dp)
                .width(300.dp),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp) // Optional for dynamic tones
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
