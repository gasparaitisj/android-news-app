package com.telesoftas.justasonboardingapp.ui.sourcelist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.ChipDefaults.filterChipColors
import androidx.compose.runtime.*
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
import com.telesoftas.justasonboardingapp.utils.Screen
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.SortBy
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalLifecycleComposeApi
@Composable
fun SourceListScreen(
    navController: NavHostController,
    viewModel: SourceListViewModel = hiltViewModel()
) {
    val newsSources by viewModel.newsSources.collectAsStateWithLifecycle()
    val sortType by viewModel.sortType.collectAsState()
    SourceListContent(
        newsSources = newsSources,
        sortType = sortType,
        onRefresh = { viewModel.getArticles() },
        onSortTypeChanged = { viewModel.sortArticles(it) },
        onSourceItemClick = { navController.navigate(Screen.NewsList.destination(it.title)) }
    )
}

@ExperimentalMaterialApi
@Composable
private fun SourceListContent(
    newsSources: Resource<List<NewsSource>>,
    sortType: SortBy,
    onRefresh: () -> Unit,
    onSortTypeChanged: (SortBy) -> Unit,
    onSourceItemClick: (NewsSource) -> Unit
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
                    isRefreshing = newsSources.status == Status.LOADING
                ),
                onRefresh = { onRefresh() },
            ) {
                val list = newsSources.getSuccessDataOrNull().orEmpty()
                if (list.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(0.5f),
                            painter = painterResource(id = R.drawable.img_empty_state_pink),
                            contentDescription = "Empty sources image"
                        )
                        Text(
                            text = "You need an internet connection to view sources.",
                            style = Typography.body2
                        )
                    }
                } else {
                    Column {
                        ChipGroupSortArticles(
                            sortType = sortType,
                            onSortTypeChanged = onSortTypeChanged
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {
                            items(list) { item ->
                                SourceItem(
                                    item = item,
                                    onSourceItemClick = { onSourceItemClick(item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Show Snackbar on network error
    val message = stringResource(id = newsSources.messageRes ?: R.string.unknown_error)
    val actionLabel = stringResource(id = R.string.source_list_screen_snackbar_dismiss)
    LaunchedEffect(newsSources.status) {
        if (newsSources.status == Status.ERROR) {
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
private fun SourceItem(
    item: NewsSource,
    onSourceItemClick: (NewsSource) -> Unit
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .clickable { onSourceItemClick(item) }
    ) {
        Text(text = item.title, style = Typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.description, style = Typography.body2)
    }
}

@ExperimentalMaterialApi
@Composable
fun ChipGroupSortArticles(
    sortType: SortBy,
    onSortTypeChanged: (SortBy) -> Unit
) {
    val chipColors = filterChipColors(
        backgroundColor = colorResource(id = R.color.chip_not_selected_background),
        contentColor = colorResource(id = R.color.chip_not_selected_content),
        selectedBackgroundColor = colorResource(id = R.color.chip_selected_background),
        selectedContentColor = colorResource(id = R.color.chip_selected_content),
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            FilterChip(
                colors = chipColors,
                modifier = Modifier.padding(horizontal = 8.dp),
                selected = sortType == SortBy.ASCENDING,
                onClick = { onSortTypeChanged(SortBy.ASCENDING) }
            ) {
                Text(stringResource(id = R.string.source_list_screen_chip_sort_ascending))
            }
            FilterChip(
                colors = chipColors,
                modifier = Modifier.padding(horizontal = 8.dp),
                selected = sortType == SortBy.DESCENDING,
                onClick = { onSortTypeChanged(SortBy.DESCENDING) }
            ) {
                Text(stringResource(id = R.string.source_list_screen_chip_sort_descending))
            }
        }
    }
}
