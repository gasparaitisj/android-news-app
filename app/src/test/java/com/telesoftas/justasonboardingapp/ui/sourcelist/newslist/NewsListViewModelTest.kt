package com.telesoftas.justasonboardingapp.ui.sourcelist.newslist

import com.telesoftas.justasonboardingapp.MainCoroutineRule
import com.telesoftas.justasonboardingapp.ui.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlePreviewResponse
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlesListResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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
                    votes = 1,
                    publishedAt = "2022-10-03",
                    category = ArticleCategory.POLITICS,
                    title = "D Title",
                    description = "D Description"
                ),
                ArticlePreviewResponse(
                    id = "1",
                    votes = 1,
                    publishedAt = "2022-10-03",
                    category = ArticleCategory.BUSINESS,
                    title = "A Title",
                    description = "A Description"
                ),
                ArticlePreviewResponse(
                    id = "2",
                    votes = 1,
                    publishedAt = "2022-10-03",
                    category = ArticleCategory.SPORTS,
                    title = "B Title",
                    description = "B Description"
                ),
                ArticlePreviewResponse(
                    id = "3",
                    votes = 1,
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
            source = null
        )
    )
    private val articlesRepository: ArticlesRepository = mockk()

    @Before
    fun setUp() {
        coEvery {
            articlesRepository.getArticles()
        } returns articlesListResponse
        coEvery {
            articlesRepository.getFavoriteArticlesFromDatabase()
        } returns flow { emit(favoriteArticles) }
        coEvery {
            articlesRepository.insertArticleToDatabase(any())
            articlesRepository.insertArticlesToDatabase(any())
        } returns Unit
        viewModel = NewsListViewModel(articlesRepository)
    }

    @Test
    fun onRefresh_updatesArticlesCorrectly() = runTest {
        advanceUntilIdle()
        val answer = listOf(
            Article(
                id = "4",
                isFavorite = false,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "D Title",
                description = "D Description",
                author = null,
                imageUrl = null,
                source = null
            ),
            Article(
                id = "1",
                isFavorite = false,
                publishedAt = "2022-10-03",
                category = ArticleCategory.BUSINESS,
                title = "A Title",
                description = "A Description",
                author = null,
                imageUrl = null,
                source = null
            ),
            Article(
                id = "2",
                isFavorite = true,
                publishedAt = "2022-10-03",
                category = ArticleCategory.SPORTS,
                title = "B Title",
                description = "B Description",
                author = null,
                imageUrl = null,
                source = null
            ),
            Article(
                id = "3",
                isFavorite = false,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "C Title",
                description = "C Description",
                author = null,
                imageUrl = null,
                source = null
            )
        )

        viewModel.onRefresh()

        assertEquals(answer, viewModel.articles.value.data)
    }

    @Test
    fun onCategoryTypeChanged_filtersArticlesCorrectly() = runTest {
        advanceUntilIdle()
        val answer = listOf(
            Article(
                id = "4",
                isFavorite = false,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "D Title",
                description = "D Description",
                author = null,
                imageUrl = null,
                source = null
            ),
            Article(
                id = "3",
                isFavorite = false,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "C Title",
                description = "C Description",
                author = null,
                imageUrl = null,
                source = null
            )
        )

        viewModel.onCategoryTypeChanged(ArticleCategory.POLITICS)

        assertEquals(answer, viewModel.articles.value.data)
    }
}
