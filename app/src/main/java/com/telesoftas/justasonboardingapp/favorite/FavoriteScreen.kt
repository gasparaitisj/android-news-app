package com.telesoftas.justasonboardingapp.favorite

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.sourcelist.newslist.ArticleItem
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.Screen

@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
fun FavoriteScreen(
    navController: NavHostController,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val articles by viewModel.articles.collectAsStateWithLifecycle()
    val filteredArticles by viewModel.filteredArticles.collectAsStateWithLifecycle()
    val searchWidgetState by viewModel.searchWidgetState
    val searchTextState by viewModel.searchTextState

    FavoriteScreenContent(
        articles = articles,
        filteredArticles = filteredArticles,
        onArticleFavoriteChanged = { article, isFavorite ->
            viewModel.onArticleFavoriteChanged(article, isFavorite)
        },
        onArticleItemClick = { article ->
            navController.navigate(Screen.NewsDetails.destination(article.id))
        },
        searchWidgetState = searchWidgetState,
        searchTextState = searchTextState,
        onTextChange = { viewModel.updateSearchTextState(newValue = it) },
        onCloseClick = { viewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED) },
        onSearchClick = { text -> viewModel.onFilterArticles(text) },
        onSearchTrigger = { viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED) }
    )
}

@Composable
fun FavoriteScreenContent(
    articles: List<Article>,
    filteredArticles: List<Article>,
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onArticleFavoriteChanged: (Article, Boolean) -> Unit,
    onArticleItemClick: (Article) -> Unit,
    onTextChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onSearchTrigger: () -> Unit
) {
    Scaffold(
        topBar = {
            MainAppBar(
                filteredArticles = filteredArticles,
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                onArticleItemClick = onArticleItemClick,
                onTextChange = onTextChange,
                onCloseClick = onCloseClick,
                onSearchClick = onSearchClick,
                onSearchTrigger = onSearchTrigger
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                content = {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(
                            isRefreshing = false
                        ),
                        onRefresh = {  },
                    ) {
                        Column {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(articles, { it.id }) { item ->
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
            )
        }
    )
}

@Composable
fun MainAppBar(
    filteredArticles: List<Article>,
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onArticleItemClick: (Article) -> Unit,
    onTextChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onSearchTrigger: () -> Unit
) {
    when (searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                onSearchClick = onSearchTrigger
            )
        }
        SearchWidgetState.OPENED -> {
            SearchAppBar(
                filteredArticles = filteredArticles,
                text = searchTextState,
                onArticleItemClick = onArticleItemClick,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClick,
                onSearchClicked = onSearchClick
            )
        }
    }
}

@Composable
fun DefaultAppBar(onSearchClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.top_app_bar_title_favorite)
            )
        },
        actions = {
            IconButton(
                onClick = { onSearchClick() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = Color.White
                )
            }
        },
        backgroundColor = colorResource(id = R.color.top_app_bar_background),
        contentColor = colorResource(id = R.color.top_app_bar_content)
    )
}

@Composable
fun SearchAppBar(
    filteredArticles: List<Article>,
    text: String,
    onArticleItemClick: (Article) -> Unit,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = colorResource(id = R.color.top_app_bar_background),
        contentColor = colorResource(id = R.color.top_app_bar_content)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = text,
            onValueChange = { onTextChange(it) },
            placeholder = {
                Text(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = "Search here...",
                    color = Color.White
                )
            },
            textStyle = TextStyle(fontSize = MaterialTheme.typography.subtitle1.fontSize),
            singleLine = true,
            leadingIcon = {
                IconButton(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.White
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onTextChange("")
                        } else {
                            onCloseClicked()
                            expanded = false
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon",
                        tint = Color.White
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                    expanded = true
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
            )
        )
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filteredArticles.forEach { article ->
                article.title?.let { title ->
                    DropdownMenuItem(onClick = { onArticleItemClick(article) }) {
                        Column {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = Typography.subtitle2
                            )
                            Text(
                                text = article.source ?: "",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = Typography.overline
                            )
                        }
                    }
                }
            }
        }
    }
}
