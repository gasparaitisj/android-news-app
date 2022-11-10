package com.telesoftas.justasonboardingapp.ui.newsdetails

import androidx.lifecycle.SavedStateHandle
import com.telesoftas.justasonboardingapp.MainCoroutineRule
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.ArticleViewData
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlePreviewResponse
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
class NewsDetailsViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: NewsDetailsViewModel
    private val article = Resource.success(
        ArticlePreviewResponse(
            id = "4",
            votes = 10,
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
        imageUrl = null,
        votes = 10
    )
    private val articlesRepository: ArticlesRepository = mockk()
    private val savedStateHandle: SavedStateHandle = mockk()

    @Before
    fun setUp() {
        every {
            checkNotNull(savedStateHandle.get("id")) as String
        } returns article.data!!.id

        coEvery {
            articlesRepository.getArticleById(article.data!!.id)
        } returns Resource.success(article.data!!.toViewData().copy(isFavorite = articleFromDatabase.isFavorite))
        coEvery {
            articlesRepository.getArticleByIdFromDatabase(article.data!!.id)
        } returns articleFromDatabase.toViewData()
    }

    @Test
    fun onViewModelInitialized_articleIsLoadedCorrectly() = runTest {
        viewModel = NewsDetailsViewModel(articlesRepository, savedStateHandle)
        advanceUntilIdle()
        val answer = Resource.success(
            ArticleViewData(
                id = "4",
                isFavorite = true,
                publishedAt = "2022-10-03",
                category = ArticleCategory.POLITICS,
                title = "D Title",
                description = "D Description",
                source = null,
                author = null,
                imageUrl = null,
                votes = 10
            )
        )

        assertEquals(answer, viewModel.state.value.article)
    }
}
