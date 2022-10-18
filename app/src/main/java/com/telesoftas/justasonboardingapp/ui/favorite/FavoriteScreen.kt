package com.telesoftas.justasonboardingapp.ui.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.ArticleItem
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.navigation.Screen
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status

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
    articles: Resource<List<Article>>,
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
                            isRefreshing = articles.status == Status.LOADING
                        ),
                        onRefresh = {},
                        swipeEnabled = false
                    ) {
                        val list = articles.getSuccessDataOrNull().orEmpty()
                        if (list.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    modifier = Modifier.fillMaxSize(0.5f),
                                    painter = painterResource(id = R.drawable.img_empty_state_white),
                                    contentDescription = "Empty favorites image"
                                )
                                Text(
                                    text = stringResource(R.string.favorite_screen_empty_state),
                                    style = Typography.body2
                                )
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(list, { it.id }) { item ->
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
                onCloseClick = onCloseClick,
                onSearchClick = onSearchClick
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
    onCloseClick: () -> Unit,
    onSearchClick: (String) -> Unit,
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
        Box {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .align(Alignment.Center),
                value = text,
                onValueChange = { onTextChange(it) },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.favorite_screen_search_text_field_placeholder),
                        color = colorResource(id = R.color.favorite_search_label),
                        style = Typography.subtitle1
                    )
                },
                textStyle = Typography.subtitle1,
                singleLine = true,
                leadingIcon = {
                    IconButton(
                        modifier = Modifier.alpha(ContentAlpha.medium),
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = colorResource(id = R.color.favorite_search_icon),
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (text.isNotEmpty()) {
                                onTextChange("")
                            } else {
                                onCloseClick()
                                expanded = false
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Icon",
                            tint = colorResource(id = R.color.favorite_search_icon),
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchClick(text)
                        if (filteredArticles.isNotEmpty()) expanded = true
                    }
                ),
                shape = RoundedCornerShape(4.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = colorResource(id = R.color.favorite_search_background),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black,
                    textColor = colorResource(id = R.color.favorite_search_text),
                )
            )
            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.favorite_search_background)),
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
                                    style = Typography.caption
                                )
                                Divider(
                                    modifier = Modifier.padding(top = 8.dp),
                                    color = colorResource(id = R.color.favorite_search_divider)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
