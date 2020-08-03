package com.lukaszgalinski.gamefuture.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.repositories.database.GamesDatabase
import com.lukaszgalinski.gamefuture.repositories.network.loadDataFromHTTP
import java.util.*
import kotlin.collections.ArrayList

private const val LAST_UPDATE_TIME_LABEL = "lastDate"
private const val DEFAULT_UPDATE_TIME = 7*24*60*60
private const val MILLISECOND_IN_SECOND = 1000
private const val TIME_BETWEEN_FILTER_REFRESH = 1000L

class MainMenuRepository {
    private var instance: MainMenuRepository? = null
    private lateinit var dataSet: ArrayList<GamesModel>

    fun getInstance(): MainMenuRepository?{
        instance ?: synchronized(this){
            instance ?: MainMenuRepository().also { instance = it }
        }
        return instance
    }

    fun getGames(context: Context): MutableLiveData<List<GamesModel>> {
        setGames(context)
        val data = MutableLiveData<List<GamesModel>>()
        data.postValue(dataSet)
        return data
    }

    private fun setGames(context: Context) {
        dataSet = ArrayList()
        val database = GamesDatabase.loadInstance(context = context).gamesDao()
        if (calculateTimeFromLastDataUpdate(context) > DEFAULT_UPDATE_TIME) {
            dataSet = loadDataFromHTTP(context)
            database.insertAll(dataSet)
            setUpdateTimeInSP(context)
        } else {
            dataSet = database.loadAll() as ArrayList<GamesModel>
        }
    }

    private fun calculateTimeFromLastDataUpdate(context: Context): Long {
        val date1 = Calendar.getInstance().time
        val date2 = readLastUpdateTimeFromSP(date1.time, context)
        return (date1.time - date2) / MILLISECOND_IN_SECOND
    }

    private fun setUpdateTimeInSP(context: Context) {
        val time = Calendar.getInstance().timeInMillis
        val sharedPreferences = context.getSharedPreferences(LAST_UPDATE_TIME_LABEL, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong(LAST_UPDATE_TIME_LABEL, time)
        editor.apply()
    }

    private fun readLastUpdateTimeFromSP(time: Long, context: Context): Long {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            LAST_UPDATE_TIME_LABEL, Context.MODE_PRIVATE)
        return sharedPreferences.getLong(LAST_UPDATE_TIME_LABEL, time)
    }

    fun filterData(query: String, context: Context): List<GamesModel>?{
        Thread.sleep(TIME_BETWEEN_FILTER_REFRESH)
        return GamesDatabase.loadInstance(context).gamesDao().filterGamesByName("%$query%")
    }
}