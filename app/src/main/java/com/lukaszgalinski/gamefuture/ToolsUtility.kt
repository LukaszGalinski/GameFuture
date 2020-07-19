package com.lukaszgalinski.gamefuture

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.lukaszgalinski.gamefuture.database.Games
import org.json.JSONArray
import org.json.JSONObject

private const val OFFSET_VALUE = 0
private const val SPAN_COUNT_PORTRAIT = 2
private const val SPAN_COUNT_LANDSCAPE = 3
private const val NAME_LABEL = "name"
private const val DESCRIPTION_LABEL = "description"
private const val PHOTO_URL_LABEL = "photoUrl"

fun decodeImage(photoUrl: String?): Bitmap {
    val imageBytes = Base64.decode(photoUrl, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, OFFSET_VALUE, imageBytes.size)
}

fun getSpanValueDependingOnScreenOrientation(context: Context): Int {
    val orientation = context.resources.configuration.orientation
    return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        SPAN_COUNT_LANDSCAPE
    } else {
        SPAN_COUNT_PORTRAIT
    }
}

fun convertFromJsonArray(gamesArray: JSONArray): ArrayList<Games> {
    val testArray = ArrayList<Games>()
    for (i in 0 until gamesArray.length()) {
        val currentGame: JSONObject = gamesArray.getJSONObject(i)
        val name = currentGame.getString(NAME_LABEL)
        val description = currentGame.getString(DESCRIPTION_LABEL)
        val photoUrl = currentGame.getString(PHOTO_URL_LABEL)
        testArray.add(Games(name = name, description = description, photo = photoUrl))
    }
    return testArray
}