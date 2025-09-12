package com.taskchecker.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

data class OnboardingPage(
    val title: String,
    val description: String,
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    Surface( // <-- Add this wrapper
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // <-- Respect current theme background
    ) {
        val pages = listOf(
            OnboardingPage(
                title = "Manage Your Tasks",
                description = "Add and delete tasks effortlessly\nto stay organized and stress-free."
            ),
            OnboardingPage(
                title = "Filter & Track",
                description = "Quickly find tasks by status:\nFinished, Unfinished, or All."
            ),
            OnboardingPage(
                title = "Swipe Actions",
                description = "Swipe right to mark as finished/unfinished.\nSwipe left to delete in a snap."
            )
        )

        var currentPage by remember { mutableStateOf(0) }

        var finishOnboarding by remember { mutableStateOf(false) }

        LaunchedEffect(finishOnboarding) {
            if (finishOnboarding) {
                onFinish()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .pointerInput(currentPage) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()

                        if (dragAmount > 50) {
                            if (currentPage > 0) {
                                currentPage--
                            }
                        } else if (dragAmount < -50) {
                            if (currentPage < pages.lastIndex) {
                                currentPage++
                            } else {
                                finishOnboarding = true
                            }
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val animationDuration = 300
            val animationEasing = LinearOutSlowInEasing

            AnimatedContent(
                targetState = pages[currentPage],
                transitionSpec = {
                    (slideInHorizontally(
                        animationSpec = tween(animationDuration, easing = animationEasing)
                    ) + fadeIn(animationSpec = tween(animationDuration, easing = animationEasing)))
                        .togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { -it },
                                animationSpec = tween(animationDuration, easing = animationEasing)
                            ) + fadeOut(animationSpec = tween(animationDuration, easing = animationEasing))
                        )
                },
                label = "PageTransition"
            ) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = page.description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Pagination Dots
            Row {
                pages.forEachIndexed { index, _ ->
                    val isSelected = index == currentPage
                    val dotSize by animateDpAsState(
                        targetValue = if (isSelected) 12.dp else 8.dp,
                        animationSpec = tween(animationDuration, easing = animationEasing),
                        label = "DotSize"
                    )
                    val dotColor = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }
        }

        // Bottom Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    if (currentPage < pages.lastIndex) {
                        currentPage++
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = CircleShape
            ) {
                Text(
                    text = if (currentPage == pages.lastIndex) "Get Started" else "Next",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}