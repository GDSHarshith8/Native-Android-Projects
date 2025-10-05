package com.literatrack.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.literatrack.datastore.UserPreferencesViewModel
import com.literatrack.presentation.BookDetailScreen.DetailScreen
import com.literatrack.presentation.BookStatus
import com.literatrack.presentation.LibScreen.LibraryScreen
import com.literatrack.presentation.SearchScreen.SearchScreen
import com.literatrack.presentation.utils.AppTheme
import com.literatrack.presentation.utils.OnboardingScreen

@Composable
fun LTnavgraph(
    navController: NavHostController,
    selectedTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    userPreferencesVM: UserPreferencesViewModel
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.Library.withFilter(BookStatus.Reading.name),
    ) {

        composable(AppRoutes.Onboarding.route) {
            OnboardingScreen(
                obPages = listOf(
                    "📚\n\nWelcome to Literatrack\n\n your simple way to track\nthe books you’re reading.",
                    "➕\n\n Find a book\nand add it to\nreading or completed or to wishlist.",
                    "📴\n\nEverything works offline.\n\nYour list is always with you,\nno sync needed.",
                    "🗑️\n\nChanged your mind?\n\nLong-press a book to remove \nit from your list.",
                    "✅\n\nMark your progress via swipes!\n\nwith a single swipe,\nfrom 'Wishlist' to 'Reading',\nfrom 'Reading' to 'Completed'."
                ),
                onFinish = {
                    userPreferencesVM.setFirstTimeDone()
                    navController.navigate(AppRoutes.Library.withFilter(BookStatus.Reading.name)) {
                        popUpTo(AppRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Library screen with optional filter
        composable(
            route = "library/{filterStatus}",
            arguments = listOf(navArgument("filterStatus") {
                type = NavType.StringType
                defaultValue = BookStatus.Reading.name
            })
        ) { backStackEntry ->
            val filterStatus =
                backStackEntry.arguments?.getString("filterStatus") ?: BookStatus.Reading.name
            LibraryScreen(
                navController = navController,
                filterStatus = filterStatus,
                selectedTheme = selectedTheme,
                onThemeChange = onThemeChange
            )
        }

        // Search screen
        composable(AppRoutes.Search.route) {
            SearchScreen(navController = navController)
        }

        // Detail screen
        composable(
            route = AppRoutes.Detail.route,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) {
            DetailScreen(navController = navController)
        }
    }
}