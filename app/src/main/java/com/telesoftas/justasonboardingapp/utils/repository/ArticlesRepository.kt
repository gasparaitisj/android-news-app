package com.telesoftas.justasonboardingapp.utils.repository

import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.sourcelist.SourceViewData
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.ArticleViewData
import com.telesoftas.justasonboardingapp.utils.data.ArticleDao
import com.telesoftas.justasonboardingapp.utils.data.SourceDao
import com.telesoftas.justasonboardingapp.utils.network.ArticlesService
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticlesRepository @Inject constructor(
    private val articlesService: ArticlesService,
    private val articleDao: ArticleDao,
    private val sourceDao: SourceDao
) {
    suspend fun getArticles(
        query: String? = null,
        page: Int? = null,
        pageSize: Int? = null,
        category: ArticleCategory? = null,
        sortBy: String? = null,
        pageNumber: Int? = null,
        xRequestId: String? = null
    ): Resource<List<ArticleViewData>> {
        val articlesFromDatabase = getFavoriteArticlesFromDatabase()
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
                val mappedResponse = Resource.success(
                    articlesListResponse.articles?.map { articlePreviewResponse ->
                        ArticleViewData(
                            id = articlePreviewResponse.id,
                            isFavorite = articlesFromDatabase.find { article ->
                                article.id == articlePreviewResponse.id
                            }?.isFavorite ?: false,
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
                mappedResponse.data?.let { insertArticlesToDatabase(it) }
                return@let mappedResponse
            } ?: Resource.error(msg = response.message())
        } catch (exception: Exception) {
            Resource.error(msgRes = R.string.network_error)
        }
    }

    suspend fun getArticleById(id: String): Resource<ArticleViewData> {
        val articleFromDatabase = getArticleByIdFromDatabase(id)
        return try {
            val response = articlesService.getArticleById(id)
            if (!response.isSuccessful) return Resource.success(articleFromDatabase)
            response.body()?.let { articlePreviewResponse ->
                return@let Resource.success(
                    ArticleViewData(
                        id = articlePreviewResponse.id,
                        isFavorite = articleFromDatabase?.isFavorite ?: false,
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
            Resource.success(articleFromDatabase)
        }
    }

    suspend fun getArticlesFromDatabase(): Resource<List<ArticleViewData>> =
        Resource.success(articleDao.getAllArticles().map { it.toArticle() })

    suspend fun getArticleByIdFromDatabase(id: String): ArticleViewData? =
        articleDao.getArticleById(id.toIntOrNull() ?: 0)?.toArticle()

    suspend fun getFavoriteArticlesFromDatabase(): List<ArticleViewData> =
        articleDao.getFavoriteArticles().map { it.toArticle() }

    suspend fun insertArticlesToDatabase(articles: List<ArticleViewData>) {
        articleDao.insertArticles(articles.map { it.toEntity() })
    }

    suspend fun insertArticleToDatabase(article: ArticleViewData) =
        articleDao.insertArticle(article.toEntity())

    suspend fun deleteArticleByIdFromDatabase(id: Int) = articleDao.deleteArticleById(id)

    suspend fun getNewsSources(): Resource<List<SourceViewData>> {
        val resource = Resource.success(
            getArticles().data?.map { article ->
                SourceViewData(
                    id = article.id,
                    title = article.title ?: "",
                    description = article.description ?: ""
                )
            } ?: listOf()
        )
        resource.data?.let { insertNewsSourcesToDatabase(it) }
        return resource
    }

    suspend fun getNewsSourcesFromDatabase(): Resource<List<SourceViewData>> =
        Resource.success(sourceDao.getAllNewsSources().map { it.toNewsSource() })

    suspend fun insertNewsSourcesToDatabase(sources: List<SourceViewData>) =
        sourceDao.insertNewsSources(sources.map { it.toEntity() })
}
