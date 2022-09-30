package com.telesoftas.justasonboardingapp.sourcelist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import kotlinx.coroutines.launch

@ExperimentalLifecycleComposeApi
@Composable
fun SourceListScreen(
    navController: NavHostController,
    viewModel: SourceListViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var newsSourceList = listOf<NewsSource>()
    val response by viewModel.articles.collectAsStateWithLifecycle(initialValue = Resource.loading())
    when (response.status) {
        Status.SUCCESS -> {
            response.data?.articles?.let { articles ->
                newsSourceList = articles.map { articlePreviewResponse ->
                    NewsSource(
                        title = articlePreviewResponse.title ?: "",
                        description = articlePreviewResponse.description ?: ""
                    )
                }
            }
        }
        Status.LOADING -> {}
        Status.ERROR -> {
            val message = stringResource(id = R.string.network_error)
            val actionLabel = stringResource(id = R.string.source_list_screen_snackbar_dismiss)
            LaunchedEffect(Unit) {
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

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    backgroundColor = colorResource(id = R.color.snackbarBackground),
                    actionColor = colorResource(id = R.color.snackbarAction),
                    contentColor = colorResource(id = R.color.snackbarContent),
                    snackbarData = data
                )
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                content = {
                    SwipeRefresh(
                        state = rememberSwipeRefreshState(response.status == Status.LOADING),
                        onRefresh = { viewModel.getArticles() },
                    ) {
                        SourceListContent(newsSourceList = newsSourceList)
                    }
                }
            )
        }
    )
}

@Composable
private fun SourceListContent(newsSourceList: List<NewsSource>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        items(newsSourceList) { item ->
            SourceItem(item = item)
        }
    }
}

@Composable
private fun SourceItem(item: NewsSource) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Text(text = item.title, style = Typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.description, style = Typography.body2)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun SourceListContentPreview() {
    val list = listOf(
        NewsSource("Lorem ipsum dolor sit amet", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamc"),
        NewsSource("Lorem ipsum dolor sit amet", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamc"),
    )
    SourceListContent(newsSourceList = list)
}
