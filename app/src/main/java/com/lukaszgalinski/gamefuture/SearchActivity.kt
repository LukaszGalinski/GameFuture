package com.lukaszgalinski.gamefuture

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukaszgalinski.gamefuture.database.GamesModel
import com.lukaszgalinski.gamefuture.database.GamesDatabase
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_menu_layout.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

private const val TAG: String = "MainMenuActivity "
private const val URL = "https://raw.githubusercontent.com/LukaszGalinski/GameFuture/master/gameFuture.json"
private const val JSON_ARRAY_LABEL = "games"
private const val LAST_UPDATE_TIME_LABEL = "lastDate"
private const val DEFAULT_UPDATE_TIME = 7*24*60*60
private const val MILLISECOND_IN_SECOND = 1000
private const val ACTIVITY_STATE_CHECKER = "state"

abstract class SearchActivity: AppCompatActivity(){
    protected var gamesList = listOf<GamesModel>()
    protected val gamesListAdapter = GamesListAdapter()
    private val compositeDisposable = CompositeDisposable()
    protected lateinit var searchEngine: SearchEngine
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)
        progressBar = findViewById(R.id.games_progressBar)
        searchEngine = SearchEngine(this)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, getSpanValueDependingOnScreenOrientation(this))
        menu_recycler.apply { layoutManager = mLayoutManager }
        menu_recycler.adapter = gamesListAdapter

        val initialData = loadInitialData(this).subscribe()
        compositeDisposable.add(initialData)
    }

    protected fun showData(result: List<GamesModel>){
        if(result.isEmpty()){
            Toast.makeText(this, this.resources.getString(R.string.no_results), Toast.LENGTH_SHORT).show()
        }
        gamesListAdapter.games = result
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ACTIVITY_STATE_CHECKER, true)
    }

    private fun loadInitialData(context: Context): Flowable<List<Long>> {
        showProgressBar()
        return Maybe.fromAction<List<Long>>{
            val database = GamesDatabase.loadInstance(context = context).gamesDao()
            if (calculateTimeFromLastDataUpdate() > DEFAULT_UPDATE_TIME) {
                val listOfTheGames = loadDataFromHTTP(this)
                database.insertAll(listOfTheGames)
                gamesList = listOfTheGames
                setUpdateTimeInSP()
            } else {
                gamesList = database.loadAll()
            }
        }.toFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                gamesListAdapter.games = gamesList
                hideProgressBar()
            }
            .doOnError {
               Toast.makeText(context, resources.getString(R.string.loading_error), Toast.LENGTH_LONG).show()
            }
    }

    private fun loadDataFromHTTP(context: Context): ArrayList<GamesModel> {
        var formattedArray = ArrayList<GamesModel>()
        val httpHandler = HttpHandler(context)
        val jsonStr = httpHandler.makeServiceCall(URL)
        try {
            val jsonObj = JSONObject(jsonStr)
            val gamesArray: JSONArray = jsonObj.getJSONArray(JSON_ARRAY_LABEL)
            formattedArray = convertFromJsonArray(this, gamesArray)
        } catch (e: JSONException) {
            Log.e(TAG, context.resources.getString(R.string.data_parsing_error) + e.message)
        }
        return formattedArray
    }

    private fun calculateTimeFromLastDataUpdate(): Long {
        val date1 = Calendar.getInstance().time
        val date2 = readLastUpdateTimeFromSP(date1.time)
        return (date1.time - date2) / MILLISECOND_IN_SECOND
    }

    private fun setUpdateTimeInSP() {
        val time = Calendar.getInstance().timeInMillis
        val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong(LAST_UPDATE_TIME_LABEL, time)
        editor.apply()
    }

    private fun readLastUpdateTimeFromSP(time: Long): Long {
        val sharedPreferences: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        return sharedPreferences.getLong(LAST_UPDATE_TIME_LABEL, time)
    }

    override fun onDestroy() {
        super.onDestroy()
        GamesDatabase.destroyInstance()
        compositeDisposable.clear()
    }

    protected fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
    }

    protected fun hideProgressBar(){
        progressBar.visibility = View.GONE
    }

}