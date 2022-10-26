package com.telesoftas.justasonboardingapp.ui.about

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.telesoftas.justasonboardingapp.R
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object CameraUtils {
    fun takePhoto(
        filenameFormat: String = "yyyy-MM-dd-HH-mm-ss-SSS",
        imageCapture: ImageCapture,
        outputDirectory: File,
        executor: Executor,
        onImageCaptured: (Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Timber.d("onError() called in imageCapture.takePicture()")
                    onError(exception)
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    onImageCaptured(savedUri)
                }
            })
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(this))
            }
        }
}

enum class CameraStatus {
    CAMERA,
    PHOTO,
    PHOTO_LOADING,
    NONE
}

enum class SnackbarStatus(@StringRes val resId: Int) {
    CAPTURE_SUCCESS(R.string.about_screen_image_capture_success),
    CAPTURE_FAILURE(R.string.about_screen_image_capture_failure),
    LOADING_FAILURE(R.string.about_screen_image_loading_failure),
    SHOW_RATIONALE(R.string.about_screen_permission_rationale)
}
