package com.telesoftas.justasonboardingapp.ui.sourcelist.newslist

import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory

data class NewsListState(
    val sourceTitle: String = "",
    val articles: Resource<List<Article>> = Resource.success(),
    val categoryType: ArticleCategory = ArticleCategory.NONE,
)
