package com.example.segmentation

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onBitmapCaptured: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Create a background executor to avoid freezing the UI
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().apply {
                    setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    // Request 640x480. CameraX will pick the closest 4:3 aspect ratio resolution.
                    // Note: In portrait, the camera might flip dimensions to 480x640 internally or keep 640x480.
                    // We handle the rotation manually below to be safe.
                    .setTargetResolution(android.util.Size(640, 480))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .apply {
                        setAnalyzer(cameraExecutor) { imageProxy: ImageProxy ->

                            // 1. Get the raw bitmap (usually Landscape/Sideways)
                            val rawBitmap = imageProxy.toBitmap()

                            // 2. Get the rotation (usually 90 degrees for portrait)
                            val rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()

                            // 3. Create a rotated bitmap if necessary
                            val finalBitmap = if (rotationDegrees != 0f) {
                                val matrix = Matrix()
                                matrix.postRotate(rotationDegrees)
                                Bitmap.createBitmap(
                                    rawBitmap,
                                    0, 0,
                                    rawBitmap.width, rawBitmap.height,
                                    matrix, true
                                )
                            } else {
                                rawBitmap
                            }

                            // 4. Pass the Portrait bitmap to the detector
                            onBitmapCaptured(finalBitmap)

                            imageProxy.close()
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (exc: Exception) {
                    // Handle exceptions
                }

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}
