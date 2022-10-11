package com.telesoftas.justasonboardingapp.newsdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.ui.theme.DarkBlue
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.Constants
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
fun NewsDetailsScreen(
    navController: NavHostController,
    viewModel: NewsDetailsViewModel = hiltViewModel()
) {
    val article by viewModel.article.collectAsState()
    NewsDetailsContent(
        article = article,
        onBackArrowClicked = { navController.navigateUp() },
        onArticleFavoriteChanged = { item, isFavorite -> viewModel.onArticleFavoriteChanged(item, isFavorite) }
    )
}

@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
fun NewsDetailsContent(
    article: Resource<Article>,
    onBackArrowClicked: () -> Boolean,
    onArticleFavoriteChanged: (Article, Boolean) -> Unit
) {
    val state = rememberCollapsingToolbarScaffoldState()
    val scrollState = rememberScrollState()
    val progress = state.toolbarState.progress

    SwipeRefresh(
        state = rememberSwipeRefreshState(
            isRefreshing = article.status == Status.LOADING
        ),
        swipeEnabled = false,
        onRefresh = {}
    ) {
        CollapsingToolbarScaffold(
            modifier = Modifier,
            state = state,
            scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
            toolbar = {
                TopAppBar(
                    modifier = Modifier
                        .road(
                            whenCollapsed = Alignment.BottomStart,
                            whenExpanded = Alignment.BottomStart
                        )
                        .pin(),
                    title = {
                        Text(
                            modifier = Modifier.alpha(if (progress <= 0.5f) 1f else progress * 2),
                            text = article.data?.title ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { onBackArrowClicked() },
                            enabled = progress <= 0.5f
                        ) {
                            Icon(
                                modifier = Modifier.alpha(if (progress <= 0.5f) 1f else progress * 2),
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    backgroundColor = colorResource(id = R.color.top_app_bar_background),
                    contentColor = colorResource(id = R.color.top_app_bar_content)
                )

                Box(
                    modifier = if (article.data?.imageUrl == null) {
                        Modifier
                            .fillMaxSize()
                            .background(colorResource(id = R.color.top_app_bar_background))
                    } else {
                        Modifier
                            .fillMaxSize()
                            .alpha(if (progress <= 0.5f) progress * 2 else 1f)
                    }
                ) {
                    AsyncImage(
                        model = "https://${article.data?.imageUrl}",
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        contentDescription = "Image",
                        contentScale = ContentScale.Crop,
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(onClick = { onBackArrowClicked() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = article.data?.title ?: "",
                            modifier = Modifier.padding(24.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = Typography.h6,
                            color = Color.White
                        )
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = scrollState)
            ) {
                article.data?.let { article ->  NewsDetailsItem(article, onArticleFavoriteChanged) }
            }
        }
    }
}

@Composable
fun NewsDetailsItem(
    item: Article,
    onArticleFavoriteChanged: (Article, Boolean) -> Unit
) {
    val selected = rememberSaveable { mutableStateOf(item.isFavorite) }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
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
        Column {
            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = item.title ?: "",
                style = Typography.h6
            )
            Text(
                modifier = Modifier.padding(top = 26.dp),
                text = item.description ?: "",
                style = Typography.body2,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            ReadFullArticleButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 32.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun ReadFullArticleButton(
    modifier: Modifier
) {
    val uriHandler = LocalUriHandler.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) {
        colorResource(id = R.color.button_pressed_background)
    } else {
        colorResource(id = R.color.button_not_pressed_background)
    }

    val contentColor = if (isPressed) {
        colorResource(id = R.color.button_pressed_content)
    } else {
        colorResource(id = R.color.button_not_pressed_content)
    }
    Button(
        modifier = modifier,
        onClick = { uriHandler.openUri(Constants.ORIGINAL_ARTICLE_URL) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        ),
        interactionSource = interactionSource
    ) {
        Text(
            text = stringResource(id = R.string.news_details_btn_open_link),
            style = Typography.button
        )
    }
}

@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
@Preview(showBackground = true)
fun NewsDetailsItemPreview() {
    val item = Article(
        id = "1",
        isFavorite = false,
        publishedAt = "2021-06-03T10:58:55Z",
        source = null,
        category = ArticleCategory.BUSINESS,
        author = "justasgasparaitis@one.lt",
        title = "Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        description = "Democrats have found as issue that unites their new majority and strengthens the position of Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        imageUrl = "placebear.com/200/300"
    )
    NewsDetailsItem(item = item, {_, _ ->})
}
