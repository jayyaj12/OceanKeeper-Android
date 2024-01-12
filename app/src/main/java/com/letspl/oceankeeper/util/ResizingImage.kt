package com.letspl.oceankeeper.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import timber.log.Timber
import java.io.*

class ResizingImage {
    fun convertResizeImage(context: Context, imageUri: Uri): File {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)

        val tempFile = File.createTempFile("resized_image", ".jpg", context.cacheDir)
        val fileOutputStream = FileOutputStream(tempFile)
        fileOutputStream.write(byteArrayOutputStream.toByteArray())
        fileOutputStream.close()

        Timber.e("tempFile ${tempFile.length()}")

        return tempFile
    }
}