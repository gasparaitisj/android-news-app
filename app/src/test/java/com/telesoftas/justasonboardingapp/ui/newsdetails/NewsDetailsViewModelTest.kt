package com.telesoftas.justasonboardingapp.ui.newsdetails

import androidx.lifecycle.SavedStateHandle
import com.telesoftas.justasonboardingapp.MainCoroutineRule
import com.telesoftas.justasonboardingapp.ui.sourcelist.ArticlesRepository
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlePreviewResponse
import io.mockk.coEvery
import io.mockk.every
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
class NewsDetailsViewModelTest {
    private lateinit var viewModel: NewsDetailsViewModel
    private val article = Resource.success(
        ArticlePreviewResponse(
            id = "4",
            votes = 1,
            publishedAt = "2022-10-03",
            category = ArticleCategory.POLITICS,
            title = "D Title",
            description = "D Description"
        )
    )
    private val articleFromDatabase = ArticleEntity(
        id = 4,
        isFavorite = true,
        publishedAt = "2022-10-03",
        category = ArticleCategory.POLITICS.ordinal,
        title = "D Title",
        description = "D Description",
        source = null,
        author = null,
        imageUrl = null
    )
    private val articlesRepository: ArticlesRepository = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        every {
            checkNotNull(savedStateHandle.get("id")) as String
        } returns article.data!!.id
        coEvery {
            articlesRepository.getArticleById(article.data!!.id)
        } returns article
        coEvery {
            articlesRepository.getArticleByIdFromDatabase(article.data!!.id)
        } returns flow { emit(articleFromDatabase) }
        viewModel = NewsDetailsViewModel(articlesRepository, savedStateHandle)
    }

    @Test
    fun onViewModelInitialized_articleIsLoadedCorrectly() = runTest {
        advanceUntilIdle()
        val answer = Resource.success(
            Article(
                id = "4",
                isFavorite = true,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "D Title",
                description = "D Description",
                source = null,
                author = null,
                imageUrl = null
            )
        )
        assertEquals(answer, viewModel.article.value)
    }
}
