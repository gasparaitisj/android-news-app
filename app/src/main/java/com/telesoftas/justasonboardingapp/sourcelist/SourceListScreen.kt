package com.telesoftas.justasonboardingapp.sourcelist

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
        onSortTypeChanged = { viewModel.sortArticles(it) }
    )
}

@ExperimentalMaterialApi
@Composable
private fun SourceListContent(
    newsSources: Resource<List<NewsSource>>,
    sortType: SortBy,
    onRefresh: () -> Unit,
    onSortTypeChanged: (SortBy) -> Unit
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { snackbarHostState ->
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    backgroundColor = colorResource(id = R.color.snackbarBackground),
                    actionColor = colorResource(id = R.color.snackbarAction),
                    contentColor = colorResource(id = R.color.snackbarContent),
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
                Column {
                    ChipGroupSortArticles(
                        sortType = sortType,
                        onSortTypeChanged = onSortTypeChanged
                    )
                    LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
                        items(newsSources.getSuccessDataOrNull() ?: listOf()) { item ->
                            SourceItem(item = item)
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
fun ChipGroupSortArticles(
    sortType: SortBy,
    onSortTypeChanged: (SortBy) -> Unit
) {
    val chipColors = filterChipColors(
        backgroundColor = colorResource(id = R.color.chipNotSelectedBackground),
        contentColor = colorResource(id = R.color.chipNotSelectedContent),
        selectedBackgroundColor = colorResource(id = R.color.chipSelectedBackground),
        selectedContentColor = colorResource(id = R.color.chipSelectedContent),
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            FilterChip(
                colors = chipColors,
                modifier = Modifier.padding(horizontal = 8.dp),
                selected = sortType == SortBy.ASCENDING,
                onClick = { onSortTypeChanged(SortBy.ASCENDING) }
                ) {
                Text(stringResource(id = R.string.source_list_screen_chip_sort_descending))
            }
            FilterChip(
                colors = chipColors,
                modifier = Modifier.padding(horizontal = 8.dp),
                selected = sortType == SortBy.DESCENDING,
                onClick = { onSortTypeChanged(SortBy.DESCENDING) }
            ) {
                Text(stringResource(id = R.string.source_list_screen_chip_sort_ascending))
            }
        }
    }
}
