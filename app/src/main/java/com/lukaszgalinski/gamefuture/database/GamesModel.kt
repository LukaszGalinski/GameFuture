package com.lukaszgalinski.gamefuture.database

import androidx.room.*

@Entity(tableName = "Games", indices = [Index(value = ["gameId"], unique = true)])
data class Games (@PrimaryKey @ColumnInfo(name = "gameId") var id: Int,
                  @ColumnInfo(name = "name") var name: String,
                  @ColumnInfo(name = "description") var description: String,
                  @ColumnInfo(name = "photo") var photo: String,
                  @ColumnInfo (name = "favourite") var favourite: Boolean = false
)