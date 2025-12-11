package com.example.segmentation

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.max

@Composable
fun DetectionOverlay(
    maskBitmap: Bitmap?,
    modifier: Modifier = Modifier
) {
    if (maskBitmap == null) return

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val imageBitmap = maskBitmap.asImageBitmap()

        val scaleX = size.width / imageBitmap.width
        val scaleY = size.height / imageBitmap.height
        val scale = max(scaleX, scaleY)

        val drawWidth = imageBitmap.width * scale
        val drawHeight = imageBitmap.height * scale

        val offsetX = (size.width - drawWidth) / 2f
        val offsetY = (size.height - drawHeight) / 2f

        withTransform({
            translate(left = offsetX, top = offsetY)
            scale(scaleX = scale, scaleY = scale)
        }) {
            // Draw the mask with partial alpha as overlay.
            drawImage(
                image = imageBitmap,
                alpha = 0.5f // semi-transparent overlay
            )
        }
    }
}
