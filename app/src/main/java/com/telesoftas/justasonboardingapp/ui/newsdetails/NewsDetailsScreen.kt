package com.telesoftas.justasonboardingapp.ui.newsdetails

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.map.GoogleMapWithClustering
import com.telesoftas.justasonboardingapp.ui.map.utils.LocationClusterItem
import com.telesoftas.justasonboardingapp.ui.sourcelist.newslist.Article
import com.telesoftas.justasonboardingapp.ui.theme.DarkBlue
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import com.telesoftas.justasonboardingapp.utils.network.Resource
import com.telesoftas.justasonboardingapp.utils.network.Status
import com.telesoftas.justasonboardingapp.utils.network.data.ArticleCategory
import com.telesoftas.justasonboardingapp.utils.other.Constants
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@ExperimentalComposeUiApi
@MapsComposeExperimentalApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
fun NewsDetailsScreen(
    navController: NavHostController,
    viewModel: NewsDetailsViewModel = hiltViewModel()
) {
    val article by viewModel.article.collectAsState()
    NewsDetailsContent(
        article = article,
        location = viewModel.location,
        onBackArrowClicked = { navController.navigateUp() },
        onArticleFavoriteChanged = { item, isFavorite ->
            viewModel.onArticleFavoriteChanged(
                article = item,
                isFavorite = isFavorite
            )
        }
    )
}

@ExperimentalComposeUiApi
@MapsComposeExperimentalApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
fun NewsDetailsContent(
    article: Resource<Article>,
    location: LocationClusterItem,
    onBackArrowClicked: () -> Unit,
    onArticleFavoriteChanged: (Article, Boolean) -> Unit
) {
    val state = rememberCollapsingToolbarScaffoldState()
    val progress = state.toolbarState.progress

    SwipeRefresh(
        state = rememberSwipeRefreshState(
            isRefreshing = article.status == Status.LOADING
        ),
        swipeEnabled = false,
        onRefresh = {}
    ) {
        CollapsingToolbarScaffold(
            modifier = Modifier,
            state = state,
            scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
            toolbar = {
                CollapsedCollapsingAppBar(
                    modifier = Modifier
                        .road(
                            whenCollapsed = Alignment.BottomStart,
                            whenExpanded = Alignment.BottomStart
                        )
                        .pin(),
                    progress, article, onBackArrowClicked
                )
                ExpandedCollapsingAppBar(article, progress, onBackArrowClicked)
            }
        ) {
            article.data?.let { article ->
                NewsDetailsItem(
                    article,
                    location,
                    onArticleFavoriteChanged
                )
            }
        }
    }
}

@Composable
private fun CollapsedCollapsingAppBar(
    modifier: Modifier,
    progress: Float,
    article: Resource<Article>,
    onBackArrowClicked: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = Modifier.alpha(if (progress <= 0.5f) 1f else progress * 2),
                text = article.data?.title ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { onBackArrowClicked() },
                enabled = progress <= 0.5f
            ) {
                Icon(
                    modifier = Modifier.alpha(if (progress <= 0.5f) 1f else progress * 2),
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        backgroundColor = colorResource(id = R.color.top_app_bar_background),
        contentColor = colorResource(id = R.color.top_app_bar_content)
    )
}

@Composable
private fun ExpandedCollapsingAppBar(
    article: Resource<Article>,
    progress: Float,
    onBackArrowClicked: () -> Unit
) {
    Box(
        modifier = if (article.data?.imageUrl == null) {
            Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.top_app_bar_background))
        } else {
            Modifier
                .fillMaxSize()
                .alpha(if (progress <= 0.5f) progress * 2 else 1f)
        }
    ) {
        AsyncImage(
            model = "https://${article.data?.imageUrl}",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            contentDescription = "Image",
            error = if (isSystemInDarkTheme()) {
                painterResource(R.drawable.img_placeholder_dark)
            } else {
                painterResource(R.drawable.img_placeholder)
            },
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { onBackArrowClicked() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = article.data?.title ?: "",
                modifier = Modifier.padding(24.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = Typography.h6,
                color = Color.White
            )
        }
    }
}

@ExperimentalComposeUiApi
@MapsComposeExperimentalApi
@Composable
fun NewsDetailsItem(
    item: Article,
    location: LocationClusterItem,
    onArticleFavoriteChanged: (Article, Boolean) -> Unit
) {
    val selected = remember { mutableStateOf(item.isFavorite) }

    val defaultCameraPosition = CameraPosition.fromLatLngZoom(location.position, 10f)
    val cameraPositionState = rememberCameraPositionState { position = defaultCameraPosition }
    var columnScrollingEnabled by remember { mutableStateOf(true) }
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            columnScrollingEnabled = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState(),
                enabled = columnScrollingEnabled
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = "${item.author} - ${item.publishedAt}",
                style = Typography.caption,
                color = DarkBlue
            )
            IconButton(
                onClick = {
                    selected.value = !selected.value
                    onArticleFavoriteChanged(item, selected.value)
                },
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
        Column {
            Text(
                modifier = Modifier.padding(top = 32.dp),
                text = item.title ?: "",
                style = Typography.h6
            )
            Text(
                modifier = Modifier.padding(top = 26.dp),
                text = item.description ?: "",
                style = Typography.body2,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            ReadFullArticleButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 32.dp)
                    .fillMaxWidth()
            )
            MapInColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((256+128).dp)
                    .padding(top = 32.dp),
                location = location,
                cameraPositionState = cameraPositionState,
                onMapTouched = {
                    columnScrollingEnabled = false
                }
            )
        }
    }
}

@MapsComposeExperimentalApi
@ExperimentalComposeUiApi
@Composable
fun MapInColumn(
    modifier: Modifier = Modifier,
    location: LocationClusterItem,
    cameraPositionState: CameraPositionState,
    onMapTouched: () -> Unit
) {
    Box(modifier = modifier) {
        GoogleMapWithClustering(
            items = listOf(location),
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter(
                    onTouchEvent = {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                onMapTouched()
                                false
                            }
                            else -> true
                        }
                    }
                ),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                compassEnabled = false,
                indoorLevelPickerEnabled = false,
                mapToolbarEnabled = false,
                myLocationButtonEnabled = false,
                rotationGesturesEnabled = false,
                scrollGesturesEnabledDuringRotateOrZoom = false,
                tiltGesturesEnabled = false,
                zoomControlsEnabled = false,
                zoomGesturesEnabled = false
            )
        )
    }
}

@Composable
fun ReadFullArticleButton(
    modifier: Modifier
) {
    val uriHandler = LocalUriHandler.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) {
        colorResource(id = R.color.button_pressed_background)
    } else {
        colorResource(id = R.color.button_not_pressed_background)
    }

    val contentColor = if (isPressed) {
        colorResource(id = R.color.button_pressed_content)
    } else {
        colorResource(id = R.color.button_not_pressed_content)
    }
    Button(
        modifier = modifier,
        onClick = { uriHandler.openUri(Constants.ORIGINAL_ARTICLE_URL) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        ),
        interactionSource = interactionSource
    ) {
        Text(
            text = stringResource(id = R.string.news_details_btn_open_link),
            style = Typography.button
        )
    }
}

@MapsComposeExperimentalApi
@ExperimentalComposeUiApi
@ExperimentalLifecycleComposeApi
@ExperimentalMaterialApi
@Composable
@Preview(showBackground = true)
fun NewsDetailsItemPreview() {
    val item = Article(
        id = "1",
        isFavorite = false,
        publishedAt = "2021-06-03T10:58:55Z",
        source = null,
        category = ArticleCategory.BUSINESS,
        author = "justasgasparaitis@one.lt",
        title = "Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        description = "Democrats have found as issue that unites their new majority and strengthens the position of Senate Minority Leader Chuck Schumer and House Speaker Nancy Polosi.",
        imageUrl = "placebear.com/200/300",
        votes = 52
    )
    NewsDetailsItem(item = item, location = LocationClusterItem(LatLng(0.0, 0.0), "", "")) { _, _ -> }
}
