package com.lukaszgalinski.gamefuture

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val DEBUG_TAG = "SQLiteDatabaseHelper"
private const val DATABASE_NAME = "gameFuture.db"
private const val DATABASE_VERSION = 1
private const val DATABASE_GAMES_TABLE = "games"
private const val COLUMN_LABEL_ID = "id"
private const val COLUMN_LABEL_NAME = "name"
private const val COLUMN_LABEL_PHOTO_URL = "photoUrl"

class SQLiteDatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private fun createTable(): String {
        return "CREATE TABLE $DATABASE_GAMES_TABLE ($COLUMN_LABEL_ID INTEGER, $COLUMN_LABEL_NAME TEXT, $COLUMN_LABEL_PHOTO_URL TEXT)"
    }

    private fun dropTable(): String {
        return "DROP TABLE IF EXISTS $DATABASE_GAMES_TABLE"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createTable())
        Log.d(DEBUG_TAG, "Database creating...")
        Log.d(DEBUG_TAG, "Table $DATABASE_GAMES_TABLE ver.$DATABASE_VERSION created")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(dropTable())
        Log.d(DEBUG_TAG, "Database updating...")
        Log.d(
            DEBUG_TAG,
            "Table $DATABASE_GAMES_TABLE updated from ver.$oldVersion to ver.$newVersion"
        )
        Log.d(DEBUG_TAG, "All data is lost.")
        onCreate(db)
    }

    fun saveGamesDataIntoTheDatabase(arrayList: ArrayList<GamesData?>) {
        val db: SQLiteDatabase = writableDatabase
        onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION+1)
        val values = ContentValues()
        for (i in arrayList.indices) {
            values.put(COLUMN_LABEL_ID, arrayList[i]?.description)
            values.put(COLUMN_LABEL_NAME, arrayList[i]?.name)
            values.put(COLUMN_LABEL_PHOTO_URL, arrayList[i]?.photoUrl)
            db.insert(DATABASE_GAMES_TABLE, null, values)
            values.clear()
        }
        db.close()
    }

    fun loadGamesDataFromTheDatabase(): ArrayList<GamesData> {
        val gamesList = ArrayList<GamesData>()
        var cursor: Cursor? = null
        val database = readableDatabase
        val selectAllQuery = "SELECT * FROM $DATABASE_GAMES_TABLE"
        try {
            cursor = database.rawQuery(selectAllQuery, null)
        } catch (e: SQLException) {
            database.execSQL(selectAllQuery)
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val userId = cursor.getString(cursor.getColumnIndex(COLUMN_LABEL_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_LABEL_NAME))
                val photoUrl = cursor.getString(cursor.getColumnIndex(COLUMN_LABEL_PHOTO_URL))
                val singleRowData = GamesData(name, userId, photoUrl)
                gamesList.add(singleRowData)
            } while (cursor.moveToNext())
            cursor.close()
            database.close()
        }
        return gamesList
    }
}