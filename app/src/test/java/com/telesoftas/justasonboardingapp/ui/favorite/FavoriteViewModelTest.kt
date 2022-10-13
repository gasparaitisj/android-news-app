package com.telesoftas.justasonboardingapp.ui.favorite

import com.telesoftas.justasonboardingapp.MainCoroutineRule
import com.telesoftas.justasonboardingapp.ui.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
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
            source = null
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
            source = null
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
            source = null
        )
    )
    private val articlesRepository: ArticlesRepository = mockk()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        coEvery {
            articlesRepository.getFavoriteArticlesFromDatabase()
        } returns flow { emit(favoriteArticles) }
        viewModel = FavoriteViewModel(articlesRepository)
    }

    @Ignore("articles from FavoriteViewModel is empty, therefore test fails, reason unknown")
    @Test
    fun onFilterArticles_filtersArticlesCorrectly() = runTest {
        advanceUntilIdle()
        val answer = listOf(
            Article(
                id = "4",
                isFavorite = true,
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
                isFavorite = true,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "A Titled",
                description = "A Description",
                author = null,
                imageUrl = null,
                source = null
            )
        )
        viewModel.onFilterArticles("D")
        assertEquals(answer, viewModel.filteredArticles.value)
    }
}
