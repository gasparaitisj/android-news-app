package com.telesoftas.justasonboardingapp.ui.sourcelist.newslist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.theme.DarkBlue
import com.telesoftas.justasonboardingapp.ui.theme.JustasOnboardingAppTheme
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.Screen
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
        onRefresh = { viewModel.onRefresh() },
        onCategoryTypeChanged = { viewModel.onCategoryTypeChanged(it) },
        onArticleItemClick = { article ->
            viewModel.onArticleClicked(article)
            navController.navigate(Screen.NewsDetails.destination(article.id))
        },
        onArticleFavoriteChanged = { article, isFavorite -> viewModel.onArticleFavoriteChanged(article, isFavorite) }
    )
}

@ExperimentalMaterialApi
@Composable
private fun NewsListContent(
    articles: Resource<List<Article>>,
    categoryType: ArticleCategory,
    onRefresh: () -> Unit,
    onCategoryTypeChanged: (ArticleCategory) -> Unit,
    onArticleItemClick: (Article) -> Unit,
    onArticleFavoriteChanged: (Article, Boolean) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { snackbarHostState ->
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    backgroundColor = colorResource(id = R.color.snackbar_background),
                    actionColor = colorResource(id = R.color.snackbar_action),
                    contentColor = colorResource(id = R.color.snackbar_content),
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
                    LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                        items(
                            items = articles.getSuccessDataOrNull().orEmpty(),
                            key = { it.id }
                        ) { item ->
                            ArticleItem(
                                item = item,
                                onArticleItemClick = { onArticleItemClick(item) },
                                onArticleFavoriteChanged = onArticleFavoriteChanged
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
fun ArticleItem(
    item: Article,
    onArticleItemClick: (Article) -> Unit,
    onArticleFavoriteChanged: (Article, Boolean) -> Unit
) {
    val selected = remember { mutableStateOf(item.isFavorite) }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onArticleItemClick(item) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${item.author} - ${item.publishedAt}",
                style = Typography.caption,
                color = DarkBlue
            )
            IconButton(
                onClick = {
                    selected.value = !selected.value
                    onArticleFavoriteChanged(item, selected.value)
                },
                content = {
                    Icon(
                        painter = if (selected.value) {
                            painterResource(id = R.drawable.btn_favorite_active)
                        } else {
                            painterResource(id = R.drawable.btn_favorite)
                        },
                        contentDescription = "Favorite"
                    )
                }
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier.size(200.dp, 140.dp),
                model = "https://${item.imageUrl}",
                contentDescription = "Image"
            )
            Text(
                text = item.title ?: "",
                style = Typography.subtitle2
            )
        }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = item.description ?: "",
            style = Typography.body2,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
    }
    Divider(
        modifier = Modifier.padding(top = 8.dp),
        thickness = 2.dp,
        color = colorResource(id = R.color.news_list_divider)
    )
}

@ExperimentalMaterialApi
@Composable
fun ChipGroupFilterArticles(
    categoryType: ArticleCategory,
    onCategoryTypeChanged: (ArticleCategory) -> Unit
) {
    val chipColors = ChipDefaults.filterChipColors(
        backgroundColor = colorResource(id = R.color.chip_not_selected_background),
        contentColor = colorResource(id = R.color.chip_not_selected_content),
        selectedBackgroundColor = colorResource(id = R.color.chip_selected_background),
        selectedContentColor = colorResource(id = R.color.chip_selected_content),
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

@Composable
@Preview(showBackground = true)
private fun ArticleItemPreview() {
    val article = Article(
        id = "1",
        isFavorite = false,
        publishedAt = "2021-06-03T10:58:55Z",
        source = null,
        category = ArticleCategory.BUSINESS,
        author = "justasgasparaitis@one.lt",
        title = "Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        description = "Democrats have found as issue that unites their new majority and strengthens the position of Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        imageUrl = "https://placebear.com/200/300"
    )
    JustasOnboardingAppTheme {
        ArticleItem(item = article, onArticleItemClick = {}, onArticleFavoriteChanged = {_, _ ->})
    }
}
