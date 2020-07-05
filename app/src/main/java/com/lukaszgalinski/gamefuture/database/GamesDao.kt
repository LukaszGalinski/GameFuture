package com.lukaszgalinski.gamefuture.database

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface GamesDao {

    @Query("SELECT * FROM games")
    fun loadAll(): List<Games>

    @Query("SELECT * FROM games WHERE name LIKE :name")
    fun filterGamesByName(name: String): List<Games>

    @Insert(onConflict = REPLACE)
    fun insertAll(games: List<Games>)


}