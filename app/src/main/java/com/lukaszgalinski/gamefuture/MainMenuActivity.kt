package com.lukaszgalinski.gamefuture

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_menu_layout.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

private const val TAG: String = "MainMenuActivity "
private const val URL = "https://pastebin.com/raw/0DEX1tc2"
private const val JSON_ARRAY_LABEL = "places"
private val arrayList = ArrayList<GamesData?>()
lateinit var adapter: GamesListAdapter
class MainMenuActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)
        GetGames(this).execute()
        showData(arrayList)
    }

    private fun showData(arrayList: ArrayList<GamesData?>){
         adapter = GamesListAdapter(this, arrayList)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
        menu_recycler.adapter = adapter
        menu_recycler.apply {
            layoutManager = mLayoutManager
        }
    }
}

class GetGames(private val context: Context): AsyncTask<Void, Void, Void>() {

    override fun onPreExecute() {
        super.onPreExecute()
        Toast.makeText(context, context.resources.getString(R.string.loading_starting), Toast.LENGTH_LONG).show()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        val httpHandler = HttpHandler(context)
        val jsonStr = httpHandler.makeServiceCall(URL)
        try {
            val jsonObj: JSONObject = JSONObject(jsonStr)
            val gamesArray: JSONArray = jsonObj.getJSONArray(JSON_ARRAY_LABEL)
            arrayList.clear()
            for (i in 0 until gamesArray.length()) {
                val currentGame: JSONObject = gamesArray.getJSONObject(i)
                val name = currentGame.getString("name")
                val element = GamesData(name, "none", "none")
                arrayList.add(element)
            }

        } catch (e: JSONException) {
            Log.e(TAG, context.resources.getString(R.string.data_parsing_error) + e.message)
        }

        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        Toast.makeText(context, context.resources.getString(R.string.loading_finished), Toast.LENGTH_LONG).show()
        adapter.notifyDataSetChanged()
    }
}



