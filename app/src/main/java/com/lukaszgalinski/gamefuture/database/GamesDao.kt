package com.lukaszgalinski.gamefuture.database

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface GamesDao {

    @Query("SELECT * FROM games")
    fun loadAll(): List<Games>

    @Query("SELECT * FROM games WHERE name LIKE :name")
    fun filterGamesByName(name: String): List<Games>

    @Insert(onConflict = REPLACE)
    fun insertAll(games: List<Games>)

    @Query("SELECT * FROM games WHERE favourite = 1")
    fun getFavouriteList(): List<Games>

    @Query("SELECT favourite FROM games WHERE gameId = :position")
    fun getFavouriteStatus(position: Int): Boolean

    @Query("UPDATE games SET favourite = :status WHERE gameId = :position")
    fun changeFavouriteStatus(position: Int, status: Boolean)
}