package com.lukaszgalinski.gamefuture

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_menu_layout.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

private const val TAG: String = "MainMenuActivity "
private const val URL = "https://raw.githubusercontent.com/LukaszGalinski/GameFuture/master/gameFuture.json"
private const val JSON_ARRAY_LABEL = "games"
private const val NAME_LABEL = "name"
private const val DESCRIPTION_LABEL = "description"
private const val PHOTO_URL_LABEL = "photoUrl"
private const val SPAN_COUNT_PORTRAIT = 2
private const val SPAN_COUNT_LANDSCAPE = 3
private const val LAST_SYNCHRONIZATION_DATE_LABEL = "lastDate"
private const val DEFAULT_UPDATE_TIME = 7*24*60*60 //week
private const val MILLISECOND_IN_SECOND = 1000
private const val PROGRESS_BAR_MAX_VALUE = 100
private val arrayList = ArrayList<GamesData?>()
lateinit var adapter: GamesListAdapter
private lateinit var progressBar: ProgressBar

class MainMenuActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)
        progressBar = findViewById(R.id.games_progressBar)
        if (getTimeDifference() > DEFAULT_UPDATE_TIME) {
            val date1 = Calendar.getInstance().time
            writeToSharedPreferences(date1.time)
            GetGames(this).execute()
        } else { 
            val newArray = SQLiteDatabaseHelper(this).loadGamesDataFromTheDatabase()
            newArray.forEachIndexed { index, _ ->
                arrayList.add(newArray[index])
            }
        }
        showData(arrayList)
    }

    private fun showData(arrayList: ArrayList<GamesData?>) { 
        adapter = GamesListAdapter(this, arrayList)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, getSpanValueDependingOnScreenOrientation())
        menu_recycler.adapter = adapter
        menu_recycler.apply {
            layoutManager = mLayoutManager
        }
        adapter.notifyDataSetChanged()
    }

    private fun getSpanValueDependingOnScreenOrientation(): Int {
        val orientation = resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) { 
            SPAN_COUNT_LANDSCAPE
        } else {
            SPAN_COUNT_PORTRAIT
        }
    }

    private fun getTimeDifference(): Long {
        val date1 = Calendar.getInstance().time
        val date2 = readFromSharedPreferences(date1.time)
        return (date1.time - date2)/ MILLISECOND_IN_SECOND
    }

    private fun writeToSharedPreferences(time: Long){
        val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong(LAST_SYNCHRONIZATION_DATE_LABEL, time)
        editor.apply()
    }

    private fun readFromSharedPreferences(time: Long): Long {
        val sharedPreferences: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        return sharedPreferences.getLong(LAST_SYNCHRONIZATION_DATE_LABEL, time)
    }
}

class GetGames(private val context: Context): AsyncTask<Void, Void, Void>() {
    override fun onPreExecute() {
        super.onPreExecute()
        progressBar.visibility = View.VISIBLE
        Toast.makeText(context, context.resources.getString(R.string.loading_starting), Toast.LENGTH_LONG).show()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        val httpHandler = HttpHandler(context)
        val jsonStr = httpHandler.makeServiceCall(URL)
        try {
            arrayList.clear()
            val jsonObj: JSONObject = JSONObject(jsonStr)
            val gamesArray: JSONArray = jsonObj.getJSONArray(JSON_ARRAY_LABEL)
            getDataFromJsonFile(gamesArray)
        } catch (e: JSONException) {
            Log.e(TAG, context.resources.getString(R.string.data_parsing_error) + e.message)
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        Toast.makeText(context, context.resources.getString(R.string.loading_finished), Toast.LENGTH_LONG).show()
        progressBar.visibility = View.GONE
        adapter.notifyDataSetChanged()
        SQLiteDatabaseHelper(context).saveGamesDataIntoTheDatabase(arrayList)
    }

    private fun getDataFromJsonFile(gamesArray: JSONArray) {
        val incrementValue = PROGRESS_BAR_MAX_VALUE/gamesArray.length()
        var currentProgress = 0
        for (i in 0 until gamesArray.length()) {
            currentProgress+=incrementValue
            progressBar.progress = currentProgress
            val currentGame: JSONObject = gamesArray.getJSONObject(i)
            val name = currentGame.getString(NAME_LABEL)
            val description = currentGame.getString(DESCRIPTION_LABEL)
            val photoUrl = currentGame.getString(PHOTO_URL_LABEL)
            val element = GamesData(name, description, photoUrl)
            arrayList.add(element)
        }
    }
}



