package com.example.objectdetection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.google.mediapipe.framework.image.BitmapExtractor
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenter
import com.google.mediapipe.tasks.vision.imagesegmenter.ImageSegmenter.ImageSegmenterOptions
import java.io.File
import java.io.FileOutputStream

class MediaPipeSegmenter(context: Context) {

    private var segmenter: ImageSegmenter? = null
    private val modelName = "deeplab_v3.tflite"

    init {
        try {
            // 1. Copy model to internal storage (cache)
            val modelPath = copyAssetToContext(context, modelName)

            // 2. FORCE CPU DELEGATE
            // This is the most critical part for your crash.
            // We explicitely tell MediaPipe NOT to touch the GPU.
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(modelPath)
                .setDelegate(Delegate.CPU)
                .build()

            // 3. Create options
            val options = ImageSegmenterOptions.builder()
                .setRunningMode(RunningMode.IMAGE)
                .setBaseOptions(baseOptions)
                .setOutputCategoryMask(true)
                .build()

            segmenter = ImageSegmenter.createFromOptions(context, options)
        } catch (e: Exception) {
            Log.e("MediaPipeSegmenter", "Error initializing segmenter", e)
        }
    }

    private fun copyAssetToContext(context: Context, fileName: String): String {
        val file = File(context.cacheDir, fileName)
        if (!file.exists()) {
            context.assets.open(fileName).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
        return file.absolutePath
    }

    fun segment(bitmap: Bitmap): Bitmap? {
        if (segmenter == null) return null

        val mpImage = BitmapImageBuilder(bitmap).build()

        // Run segmentation
        val result = segmenter!!.segment(mpImage)

        val maskMpImage = result.categoryMask().get()
        val maskBitmap = BitmapExtractor.extract(maskMpImage)

        val w = maskBitmap.width
        val h = maskBitmap.height
        val overlay = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        val pixels = IntArray(w * h)
        val maskPixels = IntArray(w * h)

        maskBitmap.getPixels(maskPixels, 0, w, 0, 0, w, h)

        for (i in 0 until w * h) {
            val classId = maskPixels[i] and 0xFF
            pixels[i] = if (classId > 0) {
                Color.argb(150, 0, 255, 0)
            } else {
                Color.TRANSPARENT
            }
        }

        overlay.setPixels(pixels, 0, w, 0, 0, w, h)
        return overlay
    }
}
