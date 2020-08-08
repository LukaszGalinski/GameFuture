package com.lukaszgalinski.gamefuture.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.repositories.database.GamesDatabase
import com.lukaszgalinski.gamefuture.repositories.network.NetworkLoading
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

private const val LAST_UPDATE_TIME_LABEL = "lastDate"
private const val DEFAULT_UPDATE_TIME = 7*24*60*60
private const val MILLISECOND_IN_SECOND = 1000
private const val CHANGE_FAVOURITE_STATUS_TAG = "Favourite status change"

class DatabaseRepository {
    private var instance: DatabaseRepository? = null
    private lateinit var dataSet: ArrayList<GamesModel>
    private var data = MutableLiveData<List<GamesModel>>()

    fun getInstance(): DatabaseRepository?{
        instance ?: synchronized(this){
            instance ?: DatabaseRepository().also { instance = it }
        }
        return instance
    }

    fun getGames(context: Context): MutableLiveData<List<GamesModel>> {
        setGames(context)
        data.postValue(dataSet)
        return data
    }

    private fun setGames(context: Context) {
        dataSet = ArrayList()
        if (calculateTimeFromLastDataUpdate(context) > DEFAULT_UPDATE_TIME) {
            updateDataFromHttp(context)
        } else {
            dataSet = GamesDatabase.loadInstance(context = context).gamesDao().loadAll() as ArrayList<GamesModel>
        }
    }

    fun updateDataFromHttp(context: Context): ArrayList<GamesModel> {
        val time = Calendar.getInstance().timeInMillis
        setUpdateTimeInSP(context, time)
        dataSet = NetworkLoading().loadHttpData() as ArrayList<GamesModel>
        GamesDatabase.loadInstance(context = context).gamesDao().insertAll(dataSet)
        return dataSet
    }

    fun filterData(data: MutableLiveData<List<GamesModel>>?, query: String, context: Context): MutableLiveData<List<GamesModel>>?{
        data?.postValue(GamesDatabase.loadInstance(context).gamesDao().filterGamesByName("%$query%"))
        return data
    }

    fun getFavouriteGames(context: Context): MutableLiveData<List<GamesModel>>?{
        data.postValue(GamesDatabase.loadInstance(context).gamesDao().getFavouriteList())
        return data
    }

    fun changeFavouriteStatus(context: Context, position: Int, status: Boolean): Disposable {
        return Observable.fromCallable {
            GamesDatabase.loadInstance(context).gamesDao().changeFavouriteStatus(position, status)
        }
            .subscribeOn(Schedulers.io())
            .doOnError { Log.w(CHANGE_FAVOURITE_STATUS_TAG,": " + it.message) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    private fun calculateTimeFromLastDataUpdate(context: Context): Long {
        val date1 = Calendar.getInstance().time
        val date2 = readLastUpdateTimeFromSP(date1.time, context)
        return (date1.time - date2) / MILLISECOND_IN_SECOND
    }

    private fun setUpdateTimeInSP(context: Context, time: Long) {
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
}