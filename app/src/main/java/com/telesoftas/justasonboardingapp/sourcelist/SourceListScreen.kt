package com.telesoftas.justasonboardingapp.sourcelist

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status

@ExperimentalLifecycleComposeApi
@Composable
fun SourceListScreen(
    navController: NavHostController,
    viewModel: SourceListViewModel = hiltViewModel()
) {
    viewModel.getArticles()
    val response by viewModel.response.collectAsStateWithLifecycle(initialValue = Resource.loading())
    val newsSourceList = mutableListOf<NewsSource>()
    when (response.status) {
        Status.SUCCESS -> {
            response.data?.articlePreviewResponses?.let { articles ->
                articles.forEach { article ->
                    newsSourceList.add(NewsSource(
                        article.title ?: "",
                        article.description ?: ""
                    ))
                    println("category: " + article.category)
                }
            }
        }
        Status.LOADING -> {
            // do nothing
        }
        Status.ERROR -> {
            Toast.makeText(
                LocalContext.current,
                stringResource(id = R.string.network_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                content = {
                    SourceListContent(newsSourceList = newsSourceList)
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
