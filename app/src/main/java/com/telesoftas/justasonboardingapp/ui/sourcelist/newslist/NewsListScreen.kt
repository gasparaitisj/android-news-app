package com.telesoftas.justasonboardingapp.ui.sourcelist.newslist

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.theme.DarkBlue
import com.telesoftas.justasonboardingapp.ui.theme.JustasOnboardingAppTheme
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.navigation.Screen
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
fun NewsListScreen(
    navController: NavHostController,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    addRefreshOnNavigation(navController = navController, onRefresh = { viewModel.onRefresh() })

    NewsListContent(
        state = state,
        isLoading = isLoading,
        onRefresh = { viewModel.onRefresh() },
        onCategoryTypeChanged = { viewModel.onCategoryTypeChanged(it) },
        onArticleItemClick = { article ->
            viewModel.onArticleClicked(article)
            navController.navigate(Screen.NewsDetails.destination(article.id))
        },
        onArticleFavoriteChanged = { article -> viewModel.onArticleFavoriteChanged(article) },
        onTopBarNavigationClicked = { navController.navigateUp() }
    )
}

@Composable
private fun addRefreshOnNavigation(
    navController: NavHostController,
    onRefresh: () -> Unit
) {
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            if (destination.route == Screen.NewsList.route) { onRefresh() }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }
}

@ExperimentalPagerApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
private fun NewsListContent(
    state: NewsListState,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onCategoryTypeChanged: (ArticleCategory) -> Unit,
    onArticleItemClick: (ArticleViewData) -> Unit,
    onArticleFavoriteChanged: (ArticleViewData) -> Unit,
    onTopBarNavigationClicked: () -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = { NewsListTopBar(state.sourceTitle, onTopBarNavigationClicked) },
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
                    isRefreshing = isLoading
                ),
                onRefresh = { onRefresh() },
            ) {
                if (state.articles.status == Status.ERROR) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(0.5f),
                            painter = painterResource(id = R.drawable.img_empty_state_black),
                            contentDescription = "Empty news image"
                        )
                        Text(
                            text = stringResource(id = R.string.empty_state),
                            style = Typography.body2
                        )
                    }
                } else {
                    Column {
                        ChipGroupFilterArticles(
                            categoryType = state.categoryType,
                            onCategoryTypeChanged = { category ->
                                onCategoryTypeChanged(category)
                                scope.launch { lazyListState.scrollToItem(0) }
                            }
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = lazyListState
                        ) {
                            items(
                                items = state.articles.getSuccessDataOrNull().orEmpty(),
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
    }

    // Show Snackbar on network error
    val message = stringResource(id = state.articles.messageRes ?: R.string.unknown_error)
    val actionLabel = stringResource(id = R.string.source_list_screen_snackbar_dismiss)
    LaunchedEffect(state.articles.status) {
        if (state.articles.status == Status.ERROR) {
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

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
private fun NewsListTopBar(
    title: String,
    onTopBarNavigationClicked: () -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onTopBarNavigationClicked) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        backgroundColor = colorResource(id = R.color.top_app_bar_background),
        contentColor = colorResource(id = R.color.top_app_bar_content)
    )
}


@Composable
fun ArticleItem(
    item: ArticleViewData,
    onArticleItemClick: (ArticleViewData) -> Unit,
    onArticleFavoriteChanged: (ArticleViewData) -> Unit
) {
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
                modifier = Modifier.weight(1f),
                text = "${item.author} - ${item.publishedAt}",
                style = Typography.caption,
                color = DarkBlue,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(
                onClick = {
                    onArticleFavoriteChanged(item)
                },
                content = {
                    Icon(
                        painter = if (item.isFavorite) {
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
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier.size(width = 100.dp, height = 70.dp),
                model = "https://${item.imageUrl}",
                contentDescription = "Image",
                error = if (isSystemInDarkTheme()) {
                    painterResource(R.drawable.img_placeholder_dark)
                } else {
                    painterResource(R.drawable.img_placeholder)
                },
                contentScale = ContentScale.Crop
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
    val article = ArticleViewData(
        id = "1",
        isFavorite = false,
        publishedAt = "2021-06-03T10:58:55Z",
        source = null,
        category = ArticleCategory.BUSINESS,
        author = "justasgasparaitis@one.lt",
        title = "Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        description = "Democrats have found as issue that unites their new majority and strengthens the position of Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        imageUrl = "https://placebear.com/200/300",
        votes = 52
    )
    JustasOnboardingAppTheme {
        ArticleItem(item = article, onArticleItemClick = {}, onArticleFavoriteChanged = {})
    }
}
