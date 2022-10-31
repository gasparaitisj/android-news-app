package com.telesoftas.justasonboardingapp.ui.about

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.telesoftas.justasonboardingapp.R
import com.telesoftas.justasonboardingapp.ui.about.CameraUtils.getCameraProvider
import com.telesoftas.justasonboardingapp.ui.main.navigation.TopBar
import com.telesoftas.justasonboardingapp.ui.theme.Typography
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalPermissionsApi
@ExperimentalMaterialApi
@Composable
fun AboutScreen(
    viewModel: AboutViewModel = hiltViewModel()
) {
    val savedPhotoUri by viewModel.savedPhotoUri.collectAsState()
    AboutScreenContent(
        onImageSuccessfullyLoaded = { viewModel.updateSavedPhotoUri(it) },
        savedPhotoUri = savedPhotoUri
    )
}

@ExperimentalPermissionsApi
@Composable
private fun AboutScreenContent(
    onImageSuccessfullyLoaded: (Uri) -> Unit,
    savedPhotoUri: Uri?
) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val channel = remember { Channel<SnackbarStatus>(Channel.CONFLATED) }
    val context = LocalContext.current

    showSnackbar(channel, context, snackbarHostState)
    Scaffold(
        topBar = { TopBar(stringResource(id = R.string.top_app_bar_title_about)) },
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        snackbarHost = { state ->
            SnackbarHost(state) { data ->
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
            modifier = Modifier
                .padding(paddingValues)
                .padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                )
                .verticalScroll(state = scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AboutScreenDescription()
            AboutScreenNotificationsCheckbox()
            CameraContent(
                channel = channel,
                onImageSuccessfullyLoaded = onImageSuccessfullyLoaded,
                savedPhotoUri = savedPhotoUri
            )
        }
    }
}

@ExperimentalPermissionsApi
@Composable
private fun CameraContent(
    channel: Channel<SnackbarStatus>,
    onImageSuccessfullyLoaded: (Uri) -> Unit,
    savedPhotoUri: Uri?
) {
    val permissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    when (permissionState.status) {
        PermissionStatus.Granted -> {
            CameraPermissionGrantedContent(
                channel = channel,
                onImageSuccessfullyLoaded = onImageSuccessfullyLoaded,
                savedPhotoUri = savedPhotoUri
            )
        }
        is PermissionStatus.Denied -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (permissionState.status.shouldShowRationale) {
                    channel.trySend(SnackbarStatus.SHOW_RATIONALE)
                }
                SelfieButton(onClick = { permissionState.launchPermissionRequest() })
            }
        }
    }
}

@Composable
private fun CameraPermissionGrantedContent(
    channel: Channel<SnackbarStatus>,
    onImageSuccessfullyLoaded: (Uri) -> Unit,
    savedPhotoUri: Uri?
) {
    val cameraStatus = remember { mutableStateOf(CameraStatus.PHOTO_LOADING) }
    val photoUri: MutableState<Uri?> = remember { mutableStateOf(null) }
    val context = LocalContext.current
    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SelfieButton(onClick = { cameraStatus.value = CameraStatus.CAMERA })
        when (cameraStatus.value) {
            CameraStatus.NONE -> {}
            CameraStatus.PHOTO_LOADING -> {
                Text(stringResource(id = R.string.about_screen_loading_photo))
                if (savedPhotoUri != null) {
                    if (savedPhotoUri == Uri.EMPTY) {
                        cameraStatus.value = CameraStatus.NONE
                    } else {
                        photoUri.value = savedPhotoUri
                        cameraStatus.value = CameraStatus.PHOTO
                    }
                }
            }
            CameraStatus.CAMERA -> {
                CameraView(
                    outputDirectory = context.filesDir,
                    executor = cameraExecutor,
                    onImageCaptured = { uri ->
                        photoUri.value = uri
                        cameraStatus.value = CameraStatus.PHOTO
                        channel.trySend(SnackbarStatus.CAPTURE_SUCCESS)
                    },
                    onError = {
                        channel.trySend(SnackbarStatus.CAPTURE_FAILURE)
                    }
                )
            }
            CameraStatus.PHOTO -> {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(data = photoUri.value).build(),
                    contentDescription = "Your selfie",
                    onSuccess = {
                        photoUri.value?.let { onImageSuccessfullyLoaded(it) }
                    },
                    onError = {
                        channel.trySend(SnackbarStatus.LOADING_FAILURE)
                    }
                )
            }
        }
    }
    DisposableEffect(cameraExecutor) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

@Composable
private fun SelfieButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
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
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        ),
        interactionSource = interactionSource
    ) {
        Text(
            text = stringResource(id = R.string.about_screen_selfie_button),
            style = Typography.button
        )
    }
}

@Composable
private fun showSnackbar(
    channel: Channel<SnackbarStatus>,
    context: Context,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(channel) {
        channel.receiveAsFlow().collect { errorMessage ->
            snackbarHostState.showSnackbar(
                message = context.resources.getString(errorMessage.resId),
                actionLabel = context.resources.getString(R.string.source_list_screen_snackbar_dismiss)
            )
        }
    }
}

@Composable
private fun AboutScreenDescription() {
    Text(
        text = stringResource(id = R.string.about_screen_paragraph_1),
        style = Typography.subtitle2
    )
    Text(
        text = stringResource(id = R.string.about_screen_paragraph_2),
        style = Typography.body2
    )
    Text(
        text = stringResource(id = R.string.about_screen_paragraph_3),
        style = Typography.body2
    )
    Text(
        text = stringResource(id = R.string.about_screen_paragraph_4),
        style = Typography.subtitle2
    )
    Text(
        text = stringResource(id = R.string.about_screen_paragraph_5),
        style = Typography.body2
    )
}

@Composable
private fun AboutScreenNotificationsCheckbox() {
    val checkedState = remember { mutableStateOf(true) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            colors = CheckboxDefaults.colors(
                checkedColor = colorResource(R.color.checkbox),
                uncheckedColor = colorResource(R.color.checkbox)
            ),
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it }
        )
        Text(
            text = stringResource(R.string.about_screen_enable_notifications),
            style = Typography.subtitle2
        )
    }
}

@Composable
private fun CameraView(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    lensFacing: Int = CameraSelector.LENS_FACING_FRONT
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { previewView }
        )
        IconButton(
            modifier = Modifier.padding(bottom = 20.dp),
            onClick = {
                CameraUtils.takePhoto(
                    imageCapture = imageCapture,
                    outputDirectory = outputDirectory,
                    executor = executor,
                    onImageCaptured = onImageCaptured,
                    onError = onError
                )
            },
            content = {
                Icon(
                    imageVector = Icons.Sharp.Lens,
                    contentDescription = "Take picture",
                    tint = Color.White,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(1.dp)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        )
    }
}
