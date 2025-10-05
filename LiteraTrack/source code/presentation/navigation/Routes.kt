package com.literatrack.presentation.navigation

sealed class AppRoutes(val route: String) {
    object Library : AppRoutes("library") {
        fun withFilter(filterStatus: String) = "library/$filterStatus"
    }
    object Search : AppRoutes("search")
    object Detail : AppRoutes("detail/{bookId}") {
        fun createRoute(bookId: String) = "detail/$bookId"
    }
    object Onboarding : AppRoutes("onboarding")
}