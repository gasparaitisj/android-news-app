package com.telesoftas.justasonboardingapp.newsdetails

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.ui.theme.DarkBlue
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory

@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
fun NewsDetailsScreen(
    navController: NavHostController,
    viewModel: NewsDetailsViewModel = hiltViewModel()
) {
    val item = Article(
        id = "1",
        publishedAt = "2021-06-03T10:58:55Z",
        source = null,
        category = ArticleCategory.BUSINESS,
        author = "justasgasparaitis@one.lt",
        title = "Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        description = "Democrats have found as issue that unites their new majority and strengthens the position of Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        imageUrl = "https://placebear.com/200/300"
    )
    NewsDetailsContent(item)
}

@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
fun NewsDetailsContent(item: Article) {
    val selected = rememberSaveable { mutableStateOf(false) }

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
                text = "${item.author} - ${item.publishedAt}",
                style = Typography.caption,
                color = DarkBlue
            )
            IconButton(
                onClick = { selected.value = !selected.value },
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier.size(200.dp, 140.dp),
                model = "https://${item.imageUrl}",
                contentDescription = "Image"
            )
            Text(
                text = item.title ?: "",
                style = Typography.subtitle2
            )
        }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = item.description ?: "",
            style = Typography.body2,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
@Preview(showBackground = true)
fun NewsDetailsContentPreview() {
    val item = Article(
        id = "1",
        publishedAt = "2021-06-03T10:58:55Z",
        source = null,
        category = ArticleCategory.BUSINESS,
        author = "justasgasparaitis@one.lt",
        title = "Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        description = "Democrats have found as issue that unites their new majority and strengthens the position of Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        imageUrl = "placebear.com/200/300"
    )
    NewsDetailsContent(item)
}
