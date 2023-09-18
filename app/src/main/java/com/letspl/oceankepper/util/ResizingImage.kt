package com.letspl.oceankepper.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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