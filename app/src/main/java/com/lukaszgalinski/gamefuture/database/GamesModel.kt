package com.lukaszgalinski.gamefuture.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

private const val TABLE_NAME = "games"
private const val COLUMN_NAME = "name"
private const val COLUMN_DESCRIPTION = "description"
private const val COLUMN_PHOTO_URL = "photo"
private const val COLUMN_FAVOURITE = "favourite"
@Entity(tableName = TABLE_NAME, indices = [Index(value = [COLUMN_NAME], unique = true)])
data class Games (@PrimaryKey(autoGenerate = true) var id: Long = 0,
                  @ColumnInfo(name = COLUMN_NAME) var name: String,
                  @ColumnInfo(name = COLUMN_DESCRIPTION) var description: String,
                  @ColumnInfo(name = COLUMN_PHOTO_URL) var photo: String,
                  @ColumnInfo(name = COLUMN_FAVOURITE) var favourite: Int = 0
)