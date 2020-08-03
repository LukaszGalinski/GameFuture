package com.lukaszgalinski.gamefuture.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.repositories.MainMenuRepository

class MainMenuViewModel : ViewModel(){
    private var gamesLiveData: MutableLiveData<List<GamesModel>>? = null
    private var mRepo: MainMenuRepository? = null

    fun init(context: Context) {
        if(gamesLiveData != null){
            return
        }
        mRepo = MainMenuRepository().getInstance()
        gamesLiveData = mRepo?.getGames(context)
    }

    fun getGamesList(): LiveData<List<GamesModel>>? {
        return gamesLiveData
    }

    fun filterData(query: String, context: Context): MutableLiveData<List<GamesModel>>? {
        val tempData = mRepo?.filterData(query, context)
        gamesLiveData?.postValue(tempData)
        return gamesLiveData
    }
}