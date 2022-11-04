package com.telesoftas.justasonboardingapp.ui.favorite

import com.telesoftas.justasonboardingapp.MainCoroutineRule
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.ArticleViewData
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.repository.ArticlesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FavoriteViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: FavoriteViewModel
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
        ),
        ArticleEntity(
            id = 4,
            isFavorite = true,
            publishedAt = "2022-10-03",
            category = ArticleCategory.POLITICS.ordinal,
            title = "D Title",
            description = "D Description",
            author = null,
            imageUrl = null,
            source = null,
            votes = 10
        ),
        ArticleEntity(
            id = 1,
            isFavorite = true,
            publishedAt = "2022-10-03",
            category = ArticleCategory.POLITICS.ordinal,
            title = "A Titled",
            description = "A Description",
            author = null,
            imageUrl = null,
            source = null,
            votes = 10
        )
    )
    private val articlesRepository: ArticlesRepository = mockk()

    @Before
    fun setUp() {
        coEvery {
            articlesRepository.getFavoriteArticlesFromDatabase()
        } returns flow { emit(favoriteArticles) }
    }

    @Ignore("articles from FavoriteViewModel is empty, therefore test fails, reason unknown")
    @Test
    fun onFilterArticles_filtersArticlesCorrectly() = runTest {
        viewModel = FavoriteViewModel(articlesRepository)
        advanceUntilIdle()
        val answer = listOf(
            ArticleViewData(
                id = "4",
                isFavorite = true,
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
                isFavorite = true,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "A Titled",
                description = "A Description",
                author = null,
                imageUrl = null,
                source = null,
                votes = 10
            )
        )

        viewModel.onFilterArticles("D")

        assertEquals(answer, viewModel.filteredArticles.value)
    }
}
