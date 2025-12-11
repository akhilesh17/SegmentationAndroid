package com.example.segmentation

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.YuvImage
import java.io.ByteArrayOutputStream

object YuvToRgbConverter {

    fun toBitmap(nv21: ByteArray, width: Int, height: Int): Bitmap {
        val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuv.compressToJpeg(android.graphics.Rect(0, 0, width, height), 90, out)
        val jpeg = out.toByteArray()
        return android.graphics.BitmapFactory.decodeByteArray(jpeg, 0, jpeg.size)
    }
}
