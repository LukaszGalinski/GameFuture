package com.lukaszgalinski.gamefuture.repositories.network

import com.lukaszgalinski.gamefuture.models.GamesModel
import retrofit2.Call
import retrofit2.http.GET

interface RestAPI {

    @GET("/LukaszGalinski/GameFuture/master/gameFuturee.json")
    fun getGamesList(): Call<List<GamesModel>>
}