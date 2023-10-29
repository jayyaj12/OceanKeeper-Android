package com.letspl.oceankepper.util

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import timber.log.Timber
import java.io.IOException


object RotateTransform {
    // 이미지 파일로부터 Exif 정보를 읽어서 회전 각도를 얻는 함수
    fun getRotationAngle(imagePath: String): Int {
        var rotationAngle = 0
        try {
            Timber.e("imagePath $imagePath")
            val exif = ExifInterface(imagePath)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotationAngle = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> rotationAngle = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> rotationAngle = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Timber.e("rotationAngle $rotationAngle")
        return rotationAngle
    }

    // 이미지 회전
    fun rotateImage(source: Bitmap, angle: Float, imageUri: Uri? = null): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        val bitmap = MediaStore.Images.Media.getBitmap(ContextUtil.context.contentResolver, imageUri)
        return Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)
    }
}