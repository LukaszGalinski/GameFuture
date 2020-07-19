package com.lukaszgalinski.gamefuture.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATABASE_VERSION = 1
private const val DATABASE_NAME = "gamesDatabase"

@Database(entities = [(Games::class)], version = DATABASE_VERSION)
abstract class GamesDatabase: RoomDatabase() {

    abstract fun gamesDao(): GamesDao

    companion object {
        private var instance: GamesDatabase? = null
        fun loadInstance(context: Context): GamesDatabase {
            if (instance == null) {
                synchronized(this) {
                    instance =
                        Room.databaseBuilder(context, GamesDatabase::class.java, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return instance as GamesDatabase
        }

        fun destroyInstance() {
            instance = null
        }
    }
}