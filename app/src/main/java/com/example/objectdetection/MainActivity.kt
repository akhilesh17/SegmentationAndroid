package com.example.objectdetection

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.objectdetection.ui.theme.ObjectDetectionTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasCameraPermission()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            ObjectDetectionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Segmentation state
                    var maskBitmap by remember { mutableStateOf<Bitmap?>(null) }

                    // Debug toggle
                    var showDebugImage by remember { mutableStateOf(true) }

                    // Initialize MediaPipe segmenter
                    val segmenter = remember { MediaPipeSegmenter(this) }

                    Box(modifier = Modifier.fillMaxSize()) {

                        // 1) Camera preview (background)
                        CameraPreview(
                            modifier = Modifier.fillMaxSize(),
                            onBitmapCaptured = { bitmap ->
                                // Run segmentation on each frame (on analyzer thread)
                                val mask = segmenter.segment(bitmap)
                                maskBitmap = mask
                            }
                        )

                        // 2) Segmentation overlay on top of camera feed
                        DetectionOverlay(
                            maskBitmap = maskBitmap,
                            modifier = Modifier.fillMaxSize()
                        )

                        // 3) Debug mask preview in corner (small view)
                        if (showDebugImage) {
                            maskBitmap?.let { bmp ->
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "Segmentation Mask Debug",
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(16.dp)
                                        .size(150.dp)
                                        .border(2.dp, Color.Red)
                                        .background(Color.Black)
                                )
                            }
                        }

                        // 4) Toggle button to show/hide debug mask
                        Button(
                            onClick = { showDebugImage = !showDebugImage },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                        ) {
                            Text(text = if (showDebugImage) "Hide Mask" else "Show Mask")
                        }
                    }
                }
            }
        }
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                recreate()
            }
        }
}
