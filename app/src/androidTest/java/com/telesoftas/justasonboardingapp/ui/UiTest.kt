package com.telesoftas.justasonboardingapp.ui

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.telesoftas.justasonboardingapp.ui.main.MainActivity
import com.telesoftas.justasonboardingapp.ui.main.MainScreen
import com.telesoftas.justasonboardingapp.ui.theme.JustasOnboardingAppTheme
import com.telesoftas.justasonboardingapp.utils.data.ArticleDao
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.data.NewsSourceDao
import com.telesoftas.justasonboardingapp.utils.data.NewsSourceEntity
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@MapsComposeExperimentalApi
@ExperimentalComposeUiApi
@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@HiltAndroidTest
class UiTest {
    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var articleDao: ArticleDao

    @Inject
    lateinit var newsSourceDao: NewsSourceDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    // Test only works offline
    @Test
    fun fromFirstAppStart_goToNewsDetailsScreen_newsDescriptionIsDisplayedCorrectly() = runTest {
        addNewsSourcesToDatabase()
        addArticlesToDatabase()
        composeTestRule.activity.setContent {
            JustasOnboardingAppTheme {
                MainScreen(navController = rememberAnimatedNavController(), viewModel = hiltViewModel())
            }
        }
        composeTestRule.awaitIdle()

        // Click on the first news source item (SourceListScreen -> NewsListScreen)
        composeTestRule.onNodeWithText("A News Source Title").performClick()
        composeTestRule.awaitIdle()

        // Click on the first news list item (NewsListScreen -> NewsDetailsScreen)
        composeTestRule.onNodeWithText("A Article Title").performClick()
        composeTestRule.awaitIdle()

        // Assert first article description is displayed
        composeTestRule.onNodeWithText("A Article Description").assertIsDisplayed()
    }

    private suspend fun addNewsSourcesToDatabase() {
        newsSourceDao.insertNewsSources(
            listOf(
                NewsSourceEntity(1, "A News Source Title", "A News Source Description"),
                NewsSourceEntity(2, "B News Source Title", "B News Source Description"),
                NewsSourceEntity(3, "C News Source Title", "C News Source Description"),
                NewsSourceEntity(4, "D News Source Title", "D News Source Description"),
                NewsSourceEntity(5, "E News Source Title", "E News Source Description"),
                NewsSourceEntity(6, "F News Source Title", "F News Source Description"),
                NewsSourceEntity(7, "G News Source Title", "G News Source Description")
            )
        )
    }
    private suspend fun addArticlesToDatabase() {
        articleDao.insertArticles(
            listOf(
                ArticleEntity(
                    id = 4,
                    isFavorite = false,
                    publishedAt = "2022-10-03",
                    category = ArticleCategory.POLITICS.ordinal,
                    title = "D Article Title",
                    description = "D Article Description",
                    author = null,
                    imageUrl = null,
                    source = null,
                    votes = 10
                ),
                ArticleEntity(
                    id = 1,
                    isFavorite = false,
                    publishedAt = "2022-10-03",
                    category = ArticleCategory.BUSINESS.ordinal,
                    title = "A Article Title",
                    description = "A Article Description",
                    author = null,
                    imageUrl = null,
                    source = null,
                    votes = 10
                ),
                ArticleEntity(
                    id = 2,
                    isFavorite = true,
                    publishedAt = "2022-10-03",
                    category = ArticleCategory.SPORTS.ordinal,
                    title = "B Article Title",
                    description = "B Article Description",
                    author = null,
                    imageUrl = null,
                    source = null,
                    votes = 10
                ),
                ArticleEntity(
                    id = 3,
                    isFavorite = false,
                    publishedAt = "2022-10-03",
                    category = ArticleCategory.POLITICS.ordinal,
                    title = "C Article Title",
                    description = "C Article Description",
                    author = null,
                    imageUrl = null,
                    source = null,
                    votes = 10
                )
            )
        )
    }
}
