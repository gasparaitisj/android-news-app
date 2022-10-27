package com.telesoftas.justasonboardingapp.utils.navigation

import androidx.annotation.StringRes
import com.telesoftas.justasonboardingapp.R

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
        @StringRes val titleResId: Int = R.string.top_app_bar_title_source_list
        val route: String get() = routePattern
        fun destination(): String = routePattern
    }
    object NewsList : Screen("news-list/{title}") {
        @StringRes val titleResId: Int = R.string.top_app_bar_title_news_list
        val route: String get() = routePattern
        fun destination(title: String): String = "news-list/${title}"
        const val KEY_TITLE = "title"
    }
    object NewsDetails : Screen("news-details/{id}") {
        @StringRes val titleResId: Int = R.string.top_app_bar_title_news_details
        val route: String get() = routePattern
        fun destination(id: String): String = "news-details/${id}"
        const val KEY_ID = "id"
    }
    object FavoriteNewsDetails : Screen("favorite-news-details/{id}") {
        @StringRes val titleResId: Int = R.string.top_app_bar_title_news_details
        val route: String get() = routePattern
        fun destination(id: String): String = "favorite-news-details/${id}"
        const val KEY_ID = "id"
    }
    object Favorite : Screen("favorite") {
        @StringRes val titleResId: Int = R.string.top_app_bar_title_favorite
        val route: String get() = routePattern
        fun destination(): String = routePattern
    }
    object Map : Screen("map") {
        @StringRes val titleResId: Int = R.string.top_app_bar_title_map
        val route: String get() = routePattern
        fun destination(): String = routePattern
    }
    object About : Screen("about") {
        @StringRes val titleResId: Int = R.string.top_app_bar_title_about
        val route: String get() = routePattern
        fun destination(): String = routePattern
    }
}
