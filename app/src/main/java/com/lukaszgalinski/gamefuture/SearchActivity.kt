package com.lukaszgalinski.gamefuture

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukaszgalinski.gamefuture.database.Games
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
private const val NAME_LABEL = "name"
private const val DESCRIPTION_LABEL = "description"
private const val PHOTO_URL_LABEL = "photoUrl"
private const val SPAN_COUNT_PORTRAIT = 2
private const val SPAN_COUNT_LANDSCAPE = 3
private const val LAST_UPDATE_TIME_LABEL = "lastDate"
private const val DEFAULT_UPDATE_TIME = 7*24*60*60 //week
private const val MILLISECOND_IN_SECOND = 1000
private const val PROGRESS_BAR_MAX_VALUE = 100
private const val ACTIVITY_STATE_CHECKER = "state"
abstract class SearchActivity: AppCompatActivity(){
    private var gamesList = listOf<Games>()
    private val adapter = GamesListAdapter()
    private val compositeDisposable = CompositeDisposable()
    protected lateinit var searchEngine: SearchEngine

    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)
        progressBar = findViewById(R.id.games_progressBar)
        searchEngine = SearchEngine(this)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, getSpanValueDependingOnScreenOrientation())
        menu_recycler.apply { layoutManager = mLayoutManager }
        menu_recycler.adapter = adapter

        adapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onRecyclerItemPressed(position: Int) {
                showAlertWithData(gamesList[position])
            }
        })

        val initialData = loadInitialData(this).subscribe()
        compositeDisposable.add(initialData)
    }

    protected fun showData(result: List<Games>){
        if(result.isEmpty()){
            Toast.makeText(this, this.resources.getString(R.string.no_results), Toast.LENGTH_SHORT).show()
        }
        adapter.games = result
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ACTIVITY_STATE_CHECKER, true)
    }

    private fun loadInitialData(context: Context): Flowable<List<Long>> {
        return Maybe.fromAction<List<Long>>{
            val database = GamesDatabase.loadInstance(context = context).gamesDao()
            if (getTimeDifference() > DEFAULT_UPDATE_TIME) {
                val listOfTheGames = loadDataFromHTTP(this)
                database.insertAll(listOfTheGames)
                gamesList = listOfTheGames
                updateTimeInSP()
            } else {
                gamesList = database.loadAll()
            }
        }.toFlowable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                adapter.games = gamesList
            }
            .doOnError {
               Toast.makeText(context, resources.getString(R.string.loading_error), Toast.LENGTH_LONG).show()
            }
    }

    protected fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
    }

    protected fun hideProgressBar(){
        progressBar.visibility = View.GONE
    }

    private fun convertJsonToList(gamesArray: JSONArray): ArrayList<Games> {
        val testArray = ArrayList<Games>()
        val incrementValue = PROGRESS_BAR_MAX_VALUE / gamesArray.length()
        var currentProgress = 0
        for (i in 0 until gamesArray.length()) {
            currentProgress += incrementValue
            progressBar.progress = currentProgress
            val currentGame: JSONObject = gamesArray.getJSONObject(i)
            val name = currentGame.getString(NAME_LABEL)
            val description = currentGame.getString(DESCRIPTION_LABEL)
            val photoUrl = currentGame.getString(PHOTO_URL_LABEL)
            testArray.add(Games(name = name, description = description, photo = photoUrl))
        }
        return testArray
    }

    private fun loadDataFromHTTP(context: Context): ArrayList<Games> {
        var formattedArray = ArrayList<Games>()
        val httpHandler = HttpHandler(context)
        val jsonStr = httpHandler.makeServiceCall(URL)
        try {
            val jsonObj = JSONObject(jsonStr)
            val gamesArray: JSONArray = jsonObj.getJSONArray(JSON_ARRAY_LABEL)
            formattedArray = convertJsonToList(gamesArray)
        } catch (e: JSONException) {
            Log.e(TAG, context.resources.getString(R.string.data_parsing_error) + e.message)
        }
        return formattedArray
    }

    private fun showAlertWithData(item: Games) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.main_menu_game_alert)
        dialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imageView = dialog.findViewById<ImageView>(R.id.row_image)
        imageView.setImageBitmap(decodeImage(item.photo))
        imageView.clipToOutline = true
        val title = dialog.findViewById<TextView>(R.id.alert_title)
        title.text = item.name
        val moveForwardButton = dialog.findViewById<Button>(R.id.alert_move_forward)

        moveForwardButton.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, GameDetailsActivity::class.java))
            finish()

        }
        dialog.show()
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
        val date2 = readUpdateTimeFromSharedPreferences(date1.time)
        return (date1.time - date2) / MILLISECOND_IN_SECOND
    }

    private fun updateTimeInSP() {
        val time = Calendar.getInstance().timeInMillis
        val sharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putLong(LAST_UPDATE_TIME_LABEL, time)
        editor.apply()
    }

    private fun readUpdateTimeFromSharedPreferences(time: Long): Long {
        val sharedPreferences: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE)
        return sharedPreferences.getLong(LAST_UPDATE_TIME_LABEL, time)
    }

    override fun onDestroy() {
        super.onDestroy()
        GamesDatabase.destroyInstance()
        compositeDisposable.clear()
    }
}