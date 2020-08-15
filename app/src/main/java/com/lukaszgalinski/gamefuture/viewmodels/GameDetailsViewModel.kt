package com.lukaszgalinski.gamefuture.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.repositories.DatabaseRepository

class GameDetailsViewModel: ViewModel(){
    private var chosenGame: GamesModel? = null
    private var databaseRepository: DatabaseRepository? = null

    fun instance(context: Context, gameId: Int){
        if (chosenGame != null){
            return
        }
        databaseRepository = DatabaseRepository().getInstance()
        chosenGame = databaseRepository?.getGame(context, gameId)
    }

    fun getData(): GamesModel?{
        return chosenGame
    }
}