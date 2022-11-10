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
            articlesRepository.getNewsSources()
        } returns Resource.success(
            articlesListResponse.data!!.articles!!.map { article ->
                SourceViewData(
                    id = article.id,
                    title = article.title ?: "",
                    description = article.description ?: ""
                )
            }
        )
    }

    @Test
    fun getArticles_loadsArticlesCorrectly() = runTest {
        viewModel = SourceListViewModel(articlesRepository)
        advanceUntilIdle()
        val answer = listOf(
            SourceViewData("4", "D Title", "D Description"),
            SourceViewData("1", "A Title", "A Description"),
            SourceViewData("2", "B Title", "B Description"),
            SourceViewData("3", "C Title", "C Description"),
        )

        viewModel.onRefresh()

        assertEquals(answer, viewModel.state.value.sources.data)
    }

    @Test
    fun sortArticles_sortsArticlesAscendingCorrectly() = runTest {
        viewModel = SourceListViewModel(articlesRepository)
        advanceUntilIdle()
        val answer = listOf(
            SourceViewData("1", "A Title", "A Description"),
            SourceViewData("2", "B Title", "B Description"),
            SourceViewData("3", "C Title", "C Description"),
            SourceViewData("4", "D Title", "D Description"),
        )

        viewModel.sortArticles(SortBy.ASCENDING)

        assertEquals(answer, viewModel.state.value.sources.data)
    }

    @Test
    fun sortArticles_sortsArticlesDescendingCorrectly() = runTest {
        viewModel = SourceListViewModel(articlesRepository)
        advanceUntilIdle()
        val answer = listOf(
            SourceViewData("4", "D Title", "D Description"),
            SourceViewData("3", "C Title", "C Description"),
            SourceViewData("2", "B Title", "B Description"),
            SourceViewData("1", "A Title", "A Description"),
        )

        viewModel.sortArticles(SortBy.DESCENDING)

        assertEquals(answer, viewModel.state.value.sources.data)
    }
}
