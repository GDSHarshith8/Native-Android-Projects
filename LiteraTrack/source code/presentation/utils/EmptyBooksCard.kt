package com.literatrack.presentation.utils

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
import androidx.compose.ui.unit.Dp

@Composable
fun EmptyBooksSurface(
    message: String,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .defaultMinSize(minWidth = 280.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium.copy(letterSpacing = 0.5.sp)
                )
            }
        }
    }
}

@Composable
fun CenteredEmptyMessage(
    message: String,
    filterRowHeight: Dp = 70.dp,
    fabSpace: Dp = 80.dp
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = filterRowHeight,
                bottom = fabSpace
            ),
        contentAlignment = Alignment.Center
    ) {
        EmptyBooksSurface(message = message)
    }
}