package com.telesoftas.justasonboardingapp.sourcelist.newslist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import kotlinx.coroutines.launch

@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
fun NewsListScreen(
    navController: NavHostController,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val articles by viewModel.articles.collectAsStateWithLifecycle()
    val categoryType by viewModel.categoryType.collectAsStateWithLifecycle()

    NewsListContent(
        articles = articles,
        categoryType = categoryType,
        onRefresh = { viewModel.getArticles() },
        onCategoryTypeChanged = { viewModel.filterArticles(it) },
        onArticleItemClick = { (_) -> }
    )
}

@ExperimentalMaterialApi
@Composable
private fun NewsListContent(
    articles: Resource<List<Article>>,
    categoryType: ArticleCategory,
    onRefresh: () -> Unit,
    onCategoryTypeChanged: (ArticleCategory) -> Unit,
    onArticleItemClick: (Article) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { snackbarHostState ->
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    backgroundColor = colorResource(id = R.color.snackbarBackground),
                    actionColor = colorResource(id = R.color.snackbarAction),
                    contentColor = colorResource(id = R.color.snackbarContent),
                    snackbarData = data
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(
                    isRefreshing = articles.status == Status.LOADING
                ),
                onRefresh = { onRefresh() },
            ) {
                Column {
                    ChipGroupFilterArticles(
                        categoryType = categoryType,
                        onCategoryTypeChanged = onCategoryTypeChanged
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        items(articles.getSuccessDataOrNull() ?: listOf()) { item ->
                            ArticleItem(
                                item = item,
                                onArticleItemClick = { onArticleItemClick(item) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Show Snackbar on network error
    val message = stringResource(id = articles.messageRes ?: R.string.unknown_error)
    val actionLabel = stringResource(id = R.string.source_list_screen_snackbar_dismiss)
    LaunchedEffect(articles.status) {
        if (articles.status == Status.ERROR) {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    duration = SnackbarDuration.Long
                )
            }
        }
    }
}

@Composable
private fun ArticleItem(
    item: Article,
    onArticleItemClick: (Article) -> Unit
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .clickable { onArticleItemClick(item) }
    ) {
        Row {
            Text(
                text = "${item.author} - ${item.publishedAt}",
                style = Typography.h6
            )
        }

        Column {
            Image(
                painter = painterResource(id = R.drawable.tutorial_first_image),
                contentDescription = "Image"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.description ?: "", style = Typography.body2)
    }
}

@ExperimentalMaterialApi
@Composable
fun ChipGroupFilterArticles(
    categoryType: ArticleCategory,
    onCategoryTypeChanged: (ArticleCategory) -> Unit
) {
    val chipColors = ChipDefaults.filterChipColors(
        backgroundColor = colorResource(id = R.color.chipNotSelectedBackground),
        contentColor = colorResource(id = R.color.chipNotSelectedContent),
        selectedBackgroundColor = colorResource(id = R.color.chipSelectedBackground),
        selectedContentColor = colorResource(id = R.color.chipSelectedContent),
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            CategoryFilterChip(
                chipCategoryType = ArticleCategory.POLITICS,
                chipText = stringResource(id = R.string.news_list_screen_chip_category_politics),
                chipColors, categoryType, onCategoryTypeChanged
            )
            CategoryFilterChip(
                chipCategoryType = ArticleCategory.SPORTS,
                chipText = stringResource(id = R.string.news_list_screen_chip_category_sports),
                chipColors, categoryType, onCategoryTypeChanged
            )
            CategoryFilterChip(
                chipCategoryType = ArticleCategory.BUSINESS,
                chipText = stringResource(id = R.string.news_list_screen_chip_category_business),
                chipColors, categoryType, onCategoryTypeChanged
            )
            CategoryFilterChip(
                chipCategoryType = ArticleCategory.FOOD,
                chipText = stringResource(id = R.string.news_list_screen_chip_category_food),
                chipColors, categoryType, onCategoryTypeChanged
            )
            CategoryFilterChip(
                chipCategoryType = ArticleCategory.CULTURE,
                chipText = stringResource(id = R.string.news_list_screen_chip_category_culture),
                chipColors, categoryType, onCategoryTypeChanged
            )
            CategoryFilterChip(
                chipCategoryType = ArticleCategory.GAMING,
                chipText = stringResource(id = R.string.news_list_screen_chip_category_gaming),
                chipColors, categoryType, onCategoryTypeChanged
            )
            CategoryFilterChip(
                chipCategoryType = ArticleCategory.HEALTH,
                chipText = stringResource(id = R.string.news_list_screen_chip_category_health),
                chipColors, categoryType, onCategoryTypeChanged
            )
            CategoryFilterChip(
                chipCategoryType = ArticleCategory.OTHER,
                chipText = stringResource(id = R.string.news_list_screen_chip_category_other),
                chipColors, categoryType, onCategoryTypeChanged
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun CategoryFilterChip(
    chipCategoryType: ArticleCategory,
    chipText: String,
    chipColors: SelectableChipColors,
    categoryType: ArticleCategory,
    onCategoryTypeChanged: (ArticleCategory) -> Unit,
) {
    FilterChip(
        colors = chipColors,
        modifier = Modifier.padding(horizontal = 8.dp),
        selected = categoryType == chipCategoryType,
        onClick = { onCategoryTypeChanged(chipCategoryType) }
    ) {
        Text(chipText)
    }
}
