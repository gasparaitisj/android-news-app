package com.telesoftas.justasonboardingapp.utils.data

import androidx.test.filters.SmallTest
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class DiaryEntryDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: AppDatabase

    private lateinit var dao: ArticleDao

    @Before
    fun setUp() {
        hiltRule.inject()
        dao = database.articleDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertArticle() = runTest {
        val article = ArticleEntity(
            id = 1,
            isFavorite = false,
            publishedAt = "2021-06-03T10:58:55Z",
            source = null,
            category = ArticleCategory.BUSINESS.ordinal,
            author = "justasgasparaitis@one.lt",
            title = "Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
            description = "Democrats have found as issue that unites their new majority and strengthens the position of Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
            imageUrl = "placebear.com/200/300"
        )
        dao.insertArticle(article)

        val allArticles = dao.getAllArticles()
        assertTrue(allArticles.contains(article))
    }

    @Test
    fun deleteArticleById() = runTest {
        val article = ArticleEntity(
            id = 1,
            isFavorite = false,
            publishedAt = "2021-06-03T10:58:55Z",
            source = null,
            category = ArticleCategory.BUSINESS.ordinal,
            author = "justasgasparaitis@one.lt",
            title = "Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
            description = "Democrats have found as issue that unites their new majority and strengthens the position of Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
            imageUrl = "placebear.com/200/300"
        )
        dao.insertArticle(article)
        dao.deleteArticleById(article.id)

        val allArticles = dao.getAllArticles()
        assertFalse(allArticles.contains(article))
    }

    @Test
    fun getFavoriteArticles() = runTest {
        val article = ArticleEntity(
            id = 1,
            isFavorite = false,
            publishedAt = "2021-06-03T10:58:55Z",
            source = null,
            category = ArticleCategory.BUSINESS.ordinal,
            author = "justasgasparaitis@one.lt",
            title = "Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
            description = "Democrats have found as issue that unites their new majority and strengthens the position of Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
            imageUrl = "placebear.com/200/300"
        )
        val favoriteArticle = article.copy(id = 2, isFavorite = true)
        val answer = listOf(favoriteArticle)

        dao.insertArticles(
            listOf(
                article,
                article.copy(id = 3),
                favoriteArticle
            )
        )

        val allFavoriteArticles = dao.getFavoriteArticles().first()
        assertTrue(allFavoriteArticles == answer)
    }
}
