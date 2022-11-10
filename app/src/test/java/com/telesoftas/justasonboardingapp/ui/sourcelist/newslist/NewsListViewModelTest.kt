package com.telesoftas.justasonboardingapp.ui.sourcelist.newslist

import androidx.lifecycle.SavedStateHandle
import com.google.firebase.analytics.FirebaseAnalytics
import com.telesoftas.justasonboardingapp.MainCoroutineRule
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.navigation.Screen
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlePreviewResponse
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlesListResponse
import com.telesoftas.justasonboardingapp.utils.repository.ArticlesRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NewsListViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: NewsListViewModel
    private val articlesListResponse = Resource.success(
        data = ArticlesListResponse(
            totalArticles = 1,
            currentPage = 1,
            totalPages = 1,
            pageSize = 5,
            articles = listOf(
                ArticlePreviewResponse(
                    id = "4",
                    votes = 10,
                    publishedAt = "2022-10-03",
                    category = ArticleCategory.POLITICS,
                    title = "D Title",
                    description = "D Description"
                ),
                ArticlePreviewResponse(
                    id = "1",
                    votes = 10,
                    publishedAt = "2022-10-03",
                    category = ArticleCategory.BUSINESS,
                    title = "A Title",
                    description = "A Description"
                ),
                ArticlePreviewResponse(
                    id = "2",
                    votes = 10,
                    publishedAt = "2022-10-03",
                    category = ArticleCategory.SPORTS,
                    title = "B Title",
                    description = "B Description"
                ),
                ArticlePreviewResponse(
                    id = "3",
                    votes = 10,
                    publishedAt = "2022-10-03",
                    category = ArticleCategory.POLITICS,
                    title = "C Title",
                    description = "C Description"
                )
            )
        )
    )
    private val favoriteArticles = listOf(
        ArticleEntity(
            id = 2,
            isFavorite = true,
            publishedAt = "2022-10-03",
            category = ArticleCategory.SPORTS.ordinal,
            title = "B Title",
            description = "B Description",
            author = null,
            imageUrl = null,
            source = null,
            votes = 10
        )
    )
    private val articlesRepository: ArticlesRepository = mockk()
    private val firebaseAnalytics: FirebaseAnalytics = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    @Before
    fun setUp() {
        every {
            checkNotNull(savedStateHandle[Screen.NewsList.KEY_TITLE]) as String
        } returns "TITLE"
        coEvery {
            articlesRepository.getArticles()
        } returns Resource.success(articlesListResponse.data!!.articles!!.map {
            it.toViewData().copy(
                isFavorite = favoriteArticles.find { article ->
                    article.id.toString() == it.id
                }?.isFavorite ?: false,
            )
        }
        )
        coEvery {
            articlesRepository.getFavoriteArticlesFromDatabase()
        } returns favoriteArticles.map { it.toViewData() }
        coEvery {
            articlesRepository.insertArticleToDatabase(any())
            articlesRepository.insertArticlesToDatabase(any())
        } returns Unit
    }

    @Test
    fun onRefresh_updatesArticlesCorrectly() = runTest {
        viewModel = NewsListViewModel(articlesRepository, firebaseAnalytics, savedStateHandle)
        advanceUntilIdle()
        val answer = listOf(
            ArticleViewData(
                id = "4",
                isFavorite = false,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "D Title",
                description = "D Description",
                author = null,
                imageUrl = null,
                source = null,
                votes = 10
            ),
            ArticleViewData(
                id = "1",
                isFavorite = false,
                publishedAt = "2022-10-03",
                category = ArticleCategory.BUSINESS,
                title = "A Title",
                description = "A Description",
                author = null,
                imageUrl = null,
                source = null,
                votes = 10
            ),
            ArticleViewData(
                id = "2",
                isFavorite = true,
                publishedAt = "2022-10-03",
                category = ArticleCategory.SPORTS,
                title = "B Title",
                description = "B Description",
                author = null,
                imageUrl = null,
                source = null,
                votes = 10
            ),
            ArticleViewData(
                id = "3",
                isFavorite = false,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "C Title",
                description = "C Description",
                author = null,
                imageUrl = null,
                source = null,
                votes = 10
            )
        )

        viewModel.onRefresh()

        assertEquals(answer, viewModel.state.value.articles.data)
    }

    @Test
    fun onCategoryTypeChanged_filtersArticlesCorrectly() = runTest {
        viewModel = NewsListViewModel(articlesRepository, firebaseAnalytics, savedStateHandle)
        advanceUntilIdle()
        val answer = listOf(
            ArticleViewData(
                id = "4",
                isFavorite = false,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "D Title",
                description = "D Description",
                author = null,
                imageUrl = null,
                source = null,
                votes = 10
            ),
            ArticleViewData(
                id = "3",
                isFavorite = false,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "C Title",
                description = "C Description",
                author = null,
                imageUrl = null,
                source = null,
                votes = 10
            )
        )

        viewModel.onCategoryTypeChanged(ArticleCategory.POLITICS)

        assertEquals(answer, viewModel.state.value.articles.data)
    }
}
