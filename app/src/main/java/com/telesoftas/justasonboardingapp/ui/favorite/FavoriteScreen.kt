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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.ArticleItem
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.navigation.Screen

@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
fun FavoriteScreen(
    navController: NavHostController,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    FavoriteScreenContent(
        state = state,
        isLoading = isLoading,
        onArticleFavoriteChanged = { article -> viewModel.onArticleFavoriteChanged(article) },
        onArticleItemClick = { article ->
            navController.navigate(Screen.FavoriteNewsDetails.destination(article.id))
        },
        onRefresh = { viewModel.onRefresh() },
        onTextChange = { viewModel.updateSearchTextState(newValue = it) },
        onCloseClick = { viewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED) },
        onSearchClick = { text -> viewModel.onFilterArticles(text) },
        onSearchTrigger = { viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED) }
    )
    addRefreshOnNavigation(navController = navController, onRefresh = { viewModel.onRefresh() })
}

@Composable
private fun addRefreshOnNavigation(
    navController: NavHostController,
    onRefresh: () -> Unit
) {
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            if (destination.route == Screen.Favorite.route) { onRefresh() }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }
}

@Composable
fun FavoriteScreenContent(
    state: FavoriteState,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onArticleFavoriteChanged: (Article) -> Unit,
    onArticleItemClick: (Article) -> Unit,
    onTextChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onSearchTrigger: () -> Unit
) {
    Scaffold(
        topBar = {
            MainAppBar(
                state = state,
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
                            isRefreshing = isLoading
                        ),
                        onRefresh = onRefresh
                    ) {
                        val list = state.articles.getSuccessDataOrNull().orEmpty()
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
    state: FavoriteState,
    onArticleItemClick: (Article) -> Unit,
    onTextChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    onSearchClick: (String) -> Unit,
    onSearchTrigger: () -> Unit
) {
    when (state.searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                onSearchClick = onSearchTrigger
            )
        }
        SearchWidgetState.OPENED -> {
            SearchAppBar(
                filteredArticles = state.filteredArticles,
                text = state.searchTextState,
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
    val expanded = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = colorResource(id = R.color.top_app_bar_background),
        contentColor = colorResource(id = R.color.top_app_bar_content)
    ) {
        Box {
            SearchTextField(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .align(Alignment.Center),
                text = text,
                onTextChange = onTextChange,
                onCloseClick = onCloseClick,
                onExpandedChange = { expanded.value = it },
                onSearchClick = onSearchClick,
                filteredArticles = filteredArticles
            )
            DropdownMenu(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.favorite_search_background)),
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                filteredArticles.forEach { article ->
                    article.title?.let { title ->
                        SearchDropdownMenuItem(onArticleItemClick, article, title)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchDropdownMenuItem(
    onArticleItemClick: (Article) -> Unit,
    article: Article,
    title: String
) {
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

@Composable
private fun SearchTextField(
    modifier: Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onSearchClick: (String) -> Unit,
    filteredArticles: List<Article>
) {
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = onTextChange,
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
            Icon(
                modifier = Modifier.alpha(ContentAlpha.medium),
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = colorResource(id = R.color.favorite_search_icon),
            )
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    if (text.isNotEmpty()) {
                        onTextChange("")
                    } else {
                        onCloseClick()
                        onExpandedChange(false)
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
                if (filteredArticles.isNotEmpty()) onExpandedChange(true)
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
}
