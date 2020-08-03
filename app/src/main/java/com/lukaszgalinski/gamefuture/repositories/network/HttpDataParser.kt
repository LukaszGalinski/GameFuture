package com.lukaszgalinski.gamefuture.repositories.network

import android.content.Context
import android.util.Log
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.repositories.database.GamesDatabase
import com.lukaszgalinski.gamefuture.utilities.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private const val TAG: String = "MainMenuActivity "
private const val URL = "https://raw.githubusercontent.com/LukaszGalinski/GameFuture/master/gameFuture.json"
private const val JSON_ARRAY_LABEL = "games"
private const val LAST_UPDATE_TIME_LABEL = "lastDate"
private const val DEFAULT_UPDATE_TIME = 7*24*60*60
private const val MILLISECOND_IN_SECOND = 1000
private const val ACTIVITY_STATE_CHECKER = "state"
private const val TIME_BETWEEN_FILTER_REFRESH = 1000L
private const val NAME_LABEL = "name"
private const val ID_LABEL = "gameId"
private const val DESCRIPTION_LABEL = "description"
private const val PHOTO_URL_LABEL = "photoUrl"

fun loadDataFromHTTP(context: Context): ArrayList<GamesModel> {
    var formattedArray = ArrayList<GamesModel>()
    val httpHandler = HttpHandler(context)
    val jsonStr = httpHandler.makeServiceCall(URL)
    try {
        val jsonObj = JSONObject(jsonStr)
        val gamesArray: JSONArray = jsonObj.getJSONArray(JSON_ARRAY_LABEL)
        formattedArray = convertFromJsonArray(context, gamesArray)
    } catch (e: JSONException) {
        Log.e(TAG, context.resources.getString(R.string.data_parsing_error) + e.message)
    }
    return formattedArray
}

fun convertFromJsonArray(context: Context, gamesArray: JSONArray): ArrayList<GamesModel> {
    val testArray = ArrayList<GamesModel>()
    for (i in 0 until gamesArray.length()) {
        val currentGame: JSONObject = gamesArray.getJSONObject(i)
        val id = currentGame.getInt(ID_LABEL)
        val name = currentGame.getString(NAME_LABEL)
        val description = currentGame.getString(DESCRIPTION_LABEL)
        val photoUrl = currentGame.getString(PHOTO_URL_LABEL)
        val favouriteStatus = GamesDatabase.loadInstance(context).gamesDao().getFavouriteStatus(id)
        testArray.add(
            GamesModel(
                id = id,
                name = name,
                description = description,
                photo = photoUrl,
                favourite = favouriteStatus
            )
        )
    }
    return testArray
}