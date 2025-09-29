package com.taskchecker.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val cS = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
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

        val pagerState = rememberPagerState(pageCount = { pages.size })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = pages[page].title,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = pages[page].description,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Animated Pagination Dots
            Row {
                pages.forEachIndexed { index, _ ->
                    val isSelected = index == pagerState.currentPage
                    val dotSize by animateDpAsState(
                        targetValue = if (isSelected) 12.dp else 8.dp,
                        animationSpec = tween(300, easing = LinearOutSlowInEasing),
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.lastIndex) {
                        cS.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
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
                    text = if (pagerState.currentPage == pages.lastIndex) "Get Started" else "Next",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
