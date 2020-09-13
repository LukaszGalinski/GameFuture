package com.lukaszgalinski.gamefuture.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.models.ShopPricesModel
import com.lukaszgalinski.gamefuture.repositories.DatabaseRepository
import com.lukaszgalinski.gamefuture.repositories.network.getShopPrices

class GameDetailsViewModel: ViewModel(){
    private var chosenGame: GamesModel? = null
    private var databaseRepository: DatabaseRepository? = null
    private var shopPrices: List<String?>? = null

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

    fun loadPrices(shopLinks: ShopPricesModel): List<String?>{
        return getShopPrices(shopLinks)

    }

}