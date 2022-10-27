package com.telesoftas.justasonboardingapp.ui.sourcelist

import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.data.ArticleDao
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.data.NewsSourceDao
import com.telesoftas.justasonboardingapp.utils.data.NewsSourceEntity
import com.telesoftas.justasonboardingapp.utils.network.ArticlesApi
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlePreviewResponse
import com.telesoftas.justasonboardingapp.utils.network.data.ArticlesListResponse
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticlesRepository @Inject constructor(
    private val articlesApi: ArticlesApi,
    private val articleDao: ArticleDao,
    private val newsSourceDao: NewsSourceDao
) {
    suspend fun getArticles(
        query: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        category: ArticleCategory? = null,
        sortBy: String? = null,
        pageNumber: Int? = null,
        xRequestId: String? = null
    ): Resource<ArticlesListResponse> {
        return try {
            val response = articlesApi.getArticles(
                query = query,
                page = page,
                pageSize = pageSize,
                category = category,
                sortBy = sortBy,
                pageNumber = pageNumber,
                xRequestId = xRequestId
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error(msg = response.message())
            } else {
                Resource.error(msg = response.message())
            }
        } catch (exception: Exception) {
            Resource.error(msgRes = R.string.network_error)
        }
    }

    fun getArticlesRx(): Single<List<Article>> = articlesApi.getArticlesRx().map { response ->
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

    suspend fun getArticleById(id: String): Resource<ArticlePreviewResponse> {
        return try {
            val response = articlesApi.getArticleById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error(msg = response.message())
            } else {
                Resource.error(msg = response.message())
            }
        } catch (exception: Exception) {
            Resource.error(msgRes = R.string.network_error)
        }
    }

    fun getArticleByIdRx(id: String): Single<Article> = articlesApi.getArticleByIdRx(id).map {
        Article(
            id = it.id,
            isFavorite = false,
            publishedAt = it.publishedAt.replace("[\$TZ]".toRegex(), " "),
            source = it.source,
            category = it.category,
            author = it.author,
            title = it.title,
            description = it.description,
            imageUrl = it.imageUrl,
            votes = it.votes
        )
    }

    fun getNewsSourcesRx(): Single<List<NewsSource>> = articlesApi.getArticlesRx().map { response ->
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

    fun getArticleByIdFromDatabase(id: String): Flow<ArticleEntity?> {
        id.toIntOrNull()?.let { idInt ->
            return articleDao.getArticleById(idInt)
        }
        return flow { emit(null) }
    }

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
