package com.lukaszgalinski.gamefuture

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.repositories.DatabaseRepository
import com.lukaszgalinski.gamefuture.repositories.database.GamesDao
import com.lukaszgalinski.gamefuture.repositories.database.GamesDatabase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadAndWriteTest {
    private lateinit var database: GamesDatabase
    private lateinit var gamesDao: GamesDao

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GamesDatabase::class.java).build()
        gamesDao = database.gamesDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadUsersElement() {
        val testList = listOf(
            GamesModel(0, "Sekiro", "", ""),
            GamesModel(1, "Minecraft", "", ""),
            GamesModel(2, "Tomb Raider", "", ""),
            GamesModel(3, "Valorant", "", "")
        )
        gamesDao.insertAll(testList)
        val searchByName = gamesDao.filterGamesByName("Minecraft")
        val loadingAll = gamesDao.loadAll()
        assertTrue(searchByName[0].name == testList[1].name)
        assertTrue(loadingAll.size == testList.size)
    }

    @Test
    @Throws(Exception::class)
    fun readAndWriteSharedPreferences(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        DatabaseRepository().setUpdateTimeInSP(context, 3000)
        val updateTime = DatabaseRepository().readLastUpdateTimeFromSP(0, context)
        assertTrue(updateTime == 3000L)
    }
}
