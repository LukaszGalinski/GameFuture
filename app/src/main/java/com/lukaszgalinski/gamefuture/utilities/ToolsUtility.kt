package com.lukaszgalinski.gamefuture.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.repositories.database.GamesDatabase
import org.json.JSONArray
import org.json.JSONObject

private const val OFFSET_VALUE = 0


fun decodeImage(photoUrl: String?): Bitmap {
    val imageBytes = Base64.decode(photoUrl, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes,
        OFFSET_VALUE, imageBytes.size)
}

