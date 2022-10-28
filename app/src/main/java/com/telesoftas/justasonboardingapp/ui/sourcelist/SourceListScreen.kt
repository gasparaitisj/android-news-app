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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavHostController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.navigation.Screen
import com.telesoftas.justasonboardingapp.utils.network.data.SortBy
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalLifecycleComposeApi
@Composable
fun SourceListScreen(
    navController: NavHostController,
    viewModel: SourceListViewModel = hiltViewModel()
) {
    val newsSources by viewModel.newsSources.observeAsState(initial = listOf())
    val sortType by viewModel.sortType.observeAsState(initial = SortBy.NONE)
    val status by viewModel.status.observeAsState(initial = Status.LOADING)
    SourceListContent(
        newsSources = newsSources,
        status = status,
        sortType = sortType,
        onRefresh = { viewModel.onRefresh() },
        onSortTypeChanged = { viewModel.sortArticles(it) },
        onSourceItemClick = { navController.navigate(Screen.NewsList.destination(it.title)) }
    )
}

@ExperimentalMaterialApi
@Composable
private fun SourceListContent(
    newsSources: List<NewsSource>,
    status: Status,
    sortType: SortBy,
    onRefresh: () -> Unit,
    onSortTypeChanged: (SortBy) -> Unit,
    onSourceItemClick: (NewsSource) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                    isRefreshing = status == Status.LOADING
                ),
                onRefresh = { onRefresh() },
            ) {
                if (newsSources.isEmpty() && status != Status.LOADING) {
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
                            text = stringResource(id = R.string.empty_state),
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
                            items(newsSources) { item ->
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

    LaunchedEffect(status) {
        if (status == Status.ERROR) {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = context.resources.getString(R.string.network_error),
                    actionLabel = context.resources.getString(R.string.source_list_screen_snackbar_dismiss),
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
