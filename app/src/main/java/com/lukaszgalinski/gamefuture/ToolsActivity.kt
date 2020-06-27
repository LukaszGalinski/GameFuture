package com.lukaszgalinski.gamefuture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

private const val OFFSET_VALUE = 0

fun decodeImage(photoUrl: String?): Bitmap {
    val imageBytes = Base64.decode(photoUrl, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, OFFSET_VALUE, imageBytes.size)
}