package com.telesoftas.justasonboardingapp.utils.navigation

sealed class Screen(val routePattern: String) {
    object Main : Screen("main") {
        val route: String get() = routePattern
        fun destination(): String = routePattern
    }
    object Tutorial : Screen("tutorial") {
        val route: String get() = routePattern
        fun destination(): String = routePattern
    }
    object SourceList : Screen("source-list") {
        val route: String get() = routePattern
        fun destination(): String = routePattern
    }
    object NewsList : Screen("news-list/{title}") {
        val route: String get() = routePattern
        fun destination(title: String): String = "news-list/${title}"
        const val KEY_TITLE = "title"
    }
    object NewsDetails : Screen("news-details/{id}") {
        val route: String get() = routePattern
        fun destination(id: String): String = "news-details/${id}"
        const val KEY_ID = "id"
    }
    object Favorite : Screen("favorite") {
        val route: String get() = routePattern
        fun destination(): String = routePattern
    }
    object Map : Screen("map") {
        val route: String get() = routePattern
        fun destination(): String = routePattern
    }
    object About : Screen("about") {
        val route: String get() = routePattern
        fun destination(): String = routePattern
    }
}
