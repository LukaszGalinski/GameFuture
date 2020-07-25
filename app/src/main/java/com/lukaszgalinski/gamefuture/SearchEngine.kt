package com.lukaszgalinski.gamefuture

import android.content.Context
import com.lukaszgalinski.gamefuture.database.GamesModel
import com.lukaszgalinski.gamefuture.database.GamesDatabase

private const val TIME_BETWEEN_FILTER_REFRESH = 1000L
class SearchEngine (private val context: Context){
    fun search(query: String): List<GamesModel>?{
        Thread.sleep(TIME_BETWEEN_FILTER_REFRESH)
        return GamesDatabase.loadInstance(context).gamesDao().filterGamesByName("%$query%")
    }
}