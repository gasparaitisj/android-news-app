package com.telesoftas.justasonboardingapp.ui.sourcelist

import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.data.ArticleDao
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.data.NewsSourceDao
import com.telesoftas.justasonboardingapp.utils.data.NewsSourceEntity
import com.telesoftas.justasonboardingapp.utils.network.ArticlesApi
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticlesRepository @Inject constructor(
    private val articlesApi: ArticlesApi,
    private val articleDao: ArticleDao,
    private val newsSourceDao: NewsSourceDao
) {
    fun getArticles(): Single<List<Article>> = articlesApi.getArticles().map { response ->
        response.articles?.map { articleResponse ->
            Article(
                id = articleResponse.id,
                isFavorite = false,
                publishedAt = articleResponse.publishedAt.replace(
                    "[\$TZ]".toRegex(),
                    " "
                ),
                source = articleResponse.source,
                category = articleResponse.category,
                author = articleResponse.author,
                title = articleResponse.title,
                description = articleResponse.description,
                imageUrl = articleResponse.imageUrl,
                votes = articleResponse.votes
            )
        } ?: listOf()
    }

    fun getArticleById(id: String): Single<Article> =
        articlesApi.getArticleById(id).map { articleResponse ->
            Article(
                id = articleResponse.id,
                isFavorite = false,
                publishedAt = articleResponse.publishedAt.replace("[\$TZ]".toRegex(), " "),
                source = articleResponse.source,
                category = articleResponse.category,
                author = articleResponse.author,
                title = articleResponse.title,
                description = articleResponse.description,
                imageUrl = articleResponse.imageUrl,
                votes = articleResponse.votes
            )
    }

    fun getNewsSources(): Single<List<NewsSource>> = articlesApi.getArticles().map { response ->
        response.articles?.map { articleResponse ->
            NewsSource(
                id = articleResponse.id,
                title = articleResponse.title ?: "",
                description = articleResponse.description ?: ""
            )
        } ?: listOf()
    }

    suspend fun getArticlesFromDatabase(): List<ArticleEntity> {
        return articleDao.getAllArticles()
    }

    fun getArticleByIdFromDatabase(id: String): Single<Article> =
        articleDao.getArticleById(id.toIntOrNull() ?: 0).map { it.toArticle() }

    fun getFavoriteArticlesFromDatabase(): Flow<List<ArticleEntity>> {
        return articleDao.getFavoriteArticles()
    }

    suspend fun insertArticlesToDatabase(articles: List<ArticleEntity>) {
        articleDao.insertArticles(articles)
    }

    suspend fun insertArticleToDatabase(article: Article?) {
        article?.toArticleEntity()?.let { articleDao.insertArticle(it) }
    }

    suspend fun deleteArticleByIdFromDatabase(id: String) {
        id.toIntOrNull()?.let { idInt ->
            articleDao.deleteArticleById(idInt)
        }
    }

    suspend fun getNewsSourcesFromDatabase(): List<NewsSourceEntity> {
        return newsSourceDao.getAllNewsSources()
    }

    suspend fun insertNewsSourcesToDatabase(newsSources: List<NewsSourceEntity>) {
        newsSourceDao.insertNewsSources(newsSources)
    }
}
