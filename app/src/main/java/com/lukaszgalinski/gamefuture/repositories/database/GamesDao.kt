package com.lukaszgalinski.gamefuture.repositories.database

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.lukaszgalinski.gamefuture.models.GamesModel

@Dao
interface GamesDao {

    @Query("SELECT * FROM games")
    fun loadAll(): List<GamesModel>

    @Query("SELECT * FROM games WHERE name LIKE :name")
    fun filterGamesByName(name: String): List<GamesModel>

    @Insert(onConflict = REPLACE)
    fun insertAll(games: List<GamesModel>): List<Long>

    @Query("SELECT * FROM games WHERE favourite = 1")
    fun getFavouriteList(): List<GamesModel>

    @Query("SELECT favourite FROM games WHERE gameId = :position")
    fun getFavouriteStatus(position: Int): Boolean

    @Query("UPDATE games SET favourite = :status WHERE gameId = :position")
    fun changeFavouriteStatus(position: Int, status: Boolean)
}