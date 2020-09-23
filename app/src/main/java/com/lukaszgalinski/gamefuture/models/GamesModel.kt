package com.lukaszgalinski.gamefuture.models

import androidx.room.*
import com.lukaszgalinski.gamefuture.repositories.StringTypeConverter

@Entity(tableName = "Games", indices = [Index(value = ["gameId"], unique = true)])
@TypeConverters(StringTypeConverter::class)
data class GamesModel(@PrimaryKey @ColumnInfo(name = "gameId") var gameId: Int,
                      @ColumnInfo(name = "name") var name: String,
                      @ColumnInfo(name = "category") var category: String,
                      @ColumnInfo(name = "description") var description: String,
                      @ColumnInfo(name = "photo") var photo: String,
                      @ColumnInfo(name = "premiere") var premiere: String,
                      @ColumnInfo(name = "producer") var producer: String,
                      @ColumnInfo(name = "rating") var rating: Double,
                      @ColumnInfo(name = "favourite") var favourite: Boolean = false,
                      @ColumnInfo(name = "galleryLinks") var galleryLinks: ArrayList<String>,
                      @ColumnInfo(name = "shopLinks") var shopLinks: ArrayList<String>,
                      @ColumnInfo(name = "videoId") var videoId: String)

