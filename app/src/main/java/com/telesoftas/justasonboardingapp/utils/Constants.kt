package com.telesoftas.justasonboardingapp.utils

object Constants {
    const val BASE_URL = "https://justas.onboarding.lt/api/v2/"

    object Routes {
        const val TUTORIAL = "tutorial"
        const val SOURCE_LIST = "source-list"
        const val FAVORITE = "favorite"
        const val ABOUT = "about"
        const val MAIN = "main"
        const val NEWS_LIST = "news-list"
        object NewsListArguments {
            const val title = "title"
        }
    }
}
