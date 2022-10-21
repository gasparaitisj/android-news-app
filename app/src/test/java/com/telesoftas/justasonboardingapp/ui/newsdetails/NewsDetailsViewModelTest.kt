package com.telesoftas.justasonboardingapp.ui.newsdetails

import androidx.lifecycle.SavedStateHandle
import com.telesoftas.justasonboardingapp.MainCoroutineRule
import com.telesoftas.justasonboardingapp.ui.map.LocationRepository
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
    private val locationRepository: LocationRepository = LocationRepository()
    private val savedStateHandle: SavedStateHandle = mockk()

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
    }

    @Test
    fun onViewModelInitialized_articleIsLoadedCorrectly() = runTest {
        viewModel = NewsDetailsViewModel(articlesRepository, locationRepository, savedStateHandle)
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
                imageUrl = null,
                votes = 10
            )
        )

        assertEquals(answer, viewModel.article.value)
    }
}
