package com.telesoftas.justasonboardingapp.utils.repository

import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.sourcelist.NewsSource
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.data.ArticleDao
import com.telesoftas.justasonboardingapp.utils.data.NewsSourceDao
import com.telesoftas.justasonboardingapp.utils.network.ArticlesService
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticlesRepository @Inject constructor(
    private val articlesService: ArticlesService,
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
    ): Resource<List<Article>> {
        return try {
            val response = articlesService.getArticles(
                query = query,
                page = page,
                pageSize = pageSize,
                category = category,
                sortBy = sortBy,
                pageNumber = pageNumber,
                xRequestId = xRequestId
            )
            if (!response.isSuccessful) return Resource.error(msg = response.message())
            response.body()?.let { articlesListResponse ->
                return@let Resource.success(
                    articlesListResponse.articles?.map { articlePreviewResponse ->
                        Article(
                            id = articlePreviewResponse.id,
                            isFavorite = false,
                            publishedAt = articlePreviewResponse.publishedAt.replace(
                                regex = "[\$TZ]".toRegex(),
                                replacement = " "
                            ),
                            source = articlePreviewResponse.source,
                            category = articlePreviewResponse.category,
                            author = articlePreviewResponse.author,
                            title = articlePreviewResponse.title,
                            description = articlePreviewResponse.description,
                            imageUrl = articlePreviewResponse.imageUrl,
                            votes = articlePreviewResponse.votes
                        )
                    } ?: listOf()
                )
            } ?: Resource.error(msg = response.message())
        } catch (exception: Exception) {
            Resource.error(msgRes = R.string.network_error)
        }
    }

    suspend fun getArticleById(id: String): Resource<Article> {
        return try {
            val response = articlesService.getArticleById(id)
            if (!response.isSuccessful) return Resource.error(msg = response.message())
            response.body()?.let { articlePreviewResponse ->
                return@let Resource.success(
                    Article(
                        id = articlePreviewResponse.id,
                        isFavorite = false,
                        publishedAt = articlePreviewResponse.publishedAt.replace(
                            regex = "[\$TZ]".toRegex(),
                            replacement = " "
                        ),
                        source = articlePreviewResponse.source,
                        category = articlePreviewResponse.category,
                        author = articlePreviewResponse.author,
                        title = articlePreviewResponse.title,
                        description = articlePreviewResponse.description,
                        imageUrl = articlePreviewResponse.imageUrl,
                        votes = articlePreviewResponse.votes
                    )
                )
            } ?: Resource.error(msg = response.message())
        } catch (exception: Exception) {
            Resource.error(msgRes = R.string.network_error)
        }
    }

    suspend fun getArticlesFromDatabase(): Resource<List<Article>> =
        Resource.success(articleDao.getAllArticles().map { it.toArticle() })

    fun getArticleByIdFromDatabase(id: String): Flow<Article?> =
        articleDao.getArticleById(id.toIntOrNull() ?: 0).map { it?.toArticle() }

    fun getFavoriteArticlesFromDatabase(): Flow<Resource<List<Article>>> =
        articleDao.getFavoriteArticles().map { articleEntityList ->
            Resource.success(articleEntityList.map { it.toArticle() })
        }

    suspend fun insertArticlesToDatabase(articles: List<Article>) {
        articleDao.insertArticles(articles.map { it.toEntity() })
    }

    suspend fun insertArticleToDatabase(article: Article) =
        articleDao.insertArticle(article.toEntity())

    suspend fun deleteArticleByIdFromDatabase(id: Int) = articleDao.deleteArticleById(id)

    suspend fun getNewsSources(): Resource<List<NewsSource>> =
        Resource.success(
            getArticles().data?.map { article ->
                NewsSource(
                    id = article.id,
                    title = article.title ?: "",
                    description = article.description ?: ""
                )
            } ?: listOf()
        )

    suspend fun getNewsSourcesFromDatabase(): Resource<List<NewsSource>> =
        Resource.success(newsSourceDao.getAllNewsSources().map { it.toNewsSource() })

    suspend fun insertNewsSourcesToDatabase(newsSources: List<NewsSource>) =
        newsSourceDao.insertNewsSources(newsSources.map { it.toEntity() })
}
