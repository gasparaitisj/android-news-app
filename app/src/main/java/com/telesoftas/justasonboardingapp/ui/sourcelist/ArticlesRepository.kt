package com.telesoftas.justasonboardingapp.ui.sourcelist

import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.utils.data.ArticleDao
import com.telesoftas.justasonboardingapp.utils.data.ArticleEntity
import com.telesoftas.justasonboardingapp.utils.data.NewsSourceDao
import com.telesoftas.justasonboardingapp.utils.network.ArticlesApi
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
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

    fun getArticlesFromDatabase(): Single<List<Article>> =
        articleDao.getAllArticles().map { articleEntityList ->
            articleEntityList.map { articleEntity ->
                articleEntity.toArticle()
            }
        }

    fun getArticleByIdFromDatabase(id: String): Single<Article> =
        articleDao.getArticleById(id.toIntOrNull() ?: 0).map { articleEntity ->
            articleEntity.toArticle()
        }

    fun getFavoriteArticlesFromDatabase(): Flowable<List<Article>> =
        articleDao.getFavoriteArticles().map { articleEntityList ->
            articleEntityList.map { articleEntity ->
                articleEntity.toArticle()
            }
        }

    fun insertArticlesToDatabase(articles: List<ArticleEntity>) {
        articleDao
            .insertArticles(articles)
            .onTerminateDetach()
            .subscribeOn(Schedulers.io())
            .subscribe({}, Timber::e)
    }

    fun insertArticleToDatabase(article: Article?) {
        article?.toArticleEntity()?.let { articleEntity ->
            articleDao
                .insertArticle(articleEntity)
                .onTerminateDetach()
                .subscribeOn(Schedulers.io())
                .subscribe({}, Timber::e)
        }
    }

    fun deleteArticleByIdFromDatabase(id: String) {
        id.toIntOrNull()?.let { idInt ->
            articleDao
                .deleteArticleById(idInt)
                .onTerminateDetach()
                .subscribeOn(Schedulers.io())
                .subscribe({}, Timber::e)
        }
    }

    fun getNewsSourcesFromDatabase(): Single<List<NewsSource>> =
        newsSourceDao.getAllNewsSources().map { newsSourceEntityList ->
            newsSourceEntityList.map { newsSourceEntity ->
                newsSourceEntity.toNewsSource()
            }
        }


    fun insertNewsSourcesToDatabase(newsSources: List<NewsSource>) =
        newsSourceDao.insertNewsSources(newsSources.map { it.toEntity() })
}
