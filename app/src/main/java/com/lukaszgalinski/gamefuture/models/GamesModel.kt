package com.lukaszgalinski.gamefuture.models

import androidx.room.*
@Entity(tableName = "Games", indices = [Index(value = ["gameId"], unique = true)])
data class GamesModel(@PrimaryKey @ColumnInfo(name = "gameId") var gameId: Int,
                      @ColumnInfo(name = "name") var name: String,
                      @ColumnInfo(name = "description") var description: String,
                      @ColumnInfo(name = "photo") var photo: String,
                      @ColumnInfo (name = "favourite") var favourite: Boolean = false)
