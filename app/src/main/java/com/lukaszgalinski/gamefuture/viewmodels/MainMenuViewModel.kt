package com.lukaszgalinski.gamefuture.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.repositories.DatabaseRepository
import io.reactivex.disposables.Disposable

private const val TIME_BETWEEN_FILTER_REFRESH = 1000L
private const val DEFAULT_UPDATE_TIME = 7*24*60*60L
class MainMenuViewModel : ViewModel(){
    private var gamesLiveData: MutableLiveData<List<GamesModel>>? = null
    private var mRepo: DatabaseRepository? = null

    fun init(context: Context) {
        if(gamesLiveData != null){
            return
        }
        mRepo = DatabaseRepository().getInstance()
        gamesLiveData = mRepo?.getGames(context)
    }

    fun getGamesList(): LiveData<List<GamesModel>>? {
        return gamesLiveData
    }

    fun filterData(query: String, context: Context): LiveData<List<GamesModel>>? {
        Thread.sleep(TIME_BETWEEN_FILTER_REFRESH)
        mRepo?.filterData(gamesLiveData, query, context)
        return getGamesList()
    }

    fun showNoResults(context: Context){
        if (gamesLiveData?.value?.isEmpty()!!){
            Toast.makeText(context, context.resources.getString(R.string.no_results), Toast.LENGTH_SHORT).show()
        }
    }

    fun changeFavouriteStatus(context: Context, gameId: Int, status: Boolean, position: Int): Disposable? {
        gamesLiveData?.value!![position].favourite = status
        return mRepo?.changeFavouriteStatus(context, gameId, status)
    }

    fun updateRecyclerFavouriteStatus(position: Int, status: Boolean){
        gamesLiveData?.value!![position].favourite = status
    }

    fun forceDataUpdating(context: Context): ArrayList<GamesModel>? {
        val data = mRepo?.updateDataFromHttp(context)
        gamesLiveData?.postValue(data)
        return data
    }
}