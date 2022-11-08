package com.telesoftas.justasonboardingapp.ui.sourcelist

import com.telesoftas.justasonboardingapp.MainCoroutineRule
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlePreviewResponse
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlesListResponse
import com.telesoftas.justasonboardingapp.utils.network.data.SortBy
import com.telesoftas.justasonboardingapp.utils.repository.ArticlesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class SourceListViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: SourceListViewModel
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
    private val articlesRepository: ArticlesRepository = mockk()

    @Before
    fun setUp() {
        coEvery {
            articlesRepository.getArticles()
        } returns articlesListResponse
    }

    @Test
    fun getArticles_loadsArticlesCorrectly() = runTest {
        viewModel = SourceListViewModel(articlesRepository)
        advanceUntilIdle()
        val answer = listOf(
            NewsSource("4", "D Title", "D Description"),
            NewsSource("1", "A Title", "A Description"),
            NewsSource("2", "B Title", "B Description"),
            NewsSource("3", "C Title", "C Description"),
        )

        viewModel.getArticles()

        assertEquals(answer, viewModel.newsSources.value.data)
    }

    @Test
    fun sortArticles_sortsArticlesAscendingCorrectly() = runTest {
        viewModel = SourceListViewModel(articlesRepository)
        advanceUntilIdle()
        val answer = listOf(
            NewsSource("1", "A Title", "A Description"),
            NewsSource("2", "B Title", "B Description"),
            NewsSource("3", "C Title", "C Description"),
            NewsSource("4", "D Title", "D Description"),
        )

        viewModel.sortArticles(SortBy.ASCENDING)

        assertEquals(answer, viewModel.newsSources.value.data)
    }

    @Test
    fun sortArticles_sortsArticlesDescendingCorrectly() = runTest {
        viewModel = SourceListViewModel(articlesRepository)
        advanceUntilIdle()
        val answer = listOf(
            NewsSource("4", "D Title", "D Description"),
            NewsSource("3", "C Title", "C Description"),
            NewsSource("2", "B Title", "B Description"),
            NewsSource("1", "A Title", "A Description"),
        )

        viewModel.sortArticles(SortBy.DESCENDING)

        assertEquals(answer, viewModel.newsSources.value.data)
    }
}
