package com.telesoftas.justasonboardingapp.sourcelist

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
import androidx.compose.ui.res.stringResource
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
import com.telesoftas.justasonboardingapp.utils.network.data.SortBy
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
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
                        SourceListContent(newsSourceList = newsSourceList, viewModel = viewModel)
                    }
                }
            )
        }
    )
}

@ExperimentalMaterialApi
@Composable
private fun SourceListContent(
    newsSourceList: List<NewsSource>,
    viewModel: SourceListViewModel
) {
    Column {
        ChipGroupSortArticles(viewModel)
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

@ExperimentalMaterialApi
@Composable
fun ChipGroupSortArticles(viewModel: SourceListViewModel) {
    val chipDescendingState = remember { mutableStateOf(false) }
    val chipAscendingState = remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            FilterChip(
                modifier = Modifier.padding(horizontal = 8.dp),
                selected = chipAscendingState.value,
                onClick = {
                    chipAscendingState.value = !chipAscendingState.value
                    chipDescendingState.value = false
                    if (chipAscendingState.value) {
                        viewModel.sortArticles(SortBy.ASCENDING)
                    } else {
                        viewModel.getArticles()
                    }
                }) {
                Text(stringResource(id = R.string.source_list_screen_chip_sort_descending))
            }
            FilterChip(
                modifier = Modifier.padding(horizontal = 8.dp),
                selected = chipDescendingState.value,
                onClick = {
                    chipDescendingState.value = !chipDescendingState.value
                    chipAscendingState.value = false
                    if (chipDescendingState.value) {
                        viewModel.sortArticles(SortBy.DESCENDING)
                    } else {
                        viewModel.getArticles()
                    }
                }) {
                Text(stringResource(id = R.string.source_list_screen_chip_sort_ascending))
            }
        }
    }
}


//@ExperimentalMaterialApi
//@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
//@Composable
//private fun SourceListContentPreview() {
//    val list = listOf(
//        NewsSource("Lorem ipsum dolor sit amet", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamc"),
//        NewsSource("Lorem ipsum dolor sit amet", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamc"),
//    )
//    SourceListContent(newsSourceList = list)
//}
