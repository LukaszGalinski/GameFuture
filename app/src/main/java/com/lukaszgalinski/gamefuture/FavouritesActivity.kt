package com.lukaszgalinski.gamefuture

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lukaszgalinski.gamefuture.database.GamesModel
import com.lukaszgalinski.gamefuture.database.GamesDatabase
import com.lukaszgalinski.gamefuture.database.changeFavouriteStatus
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.favourites_layout.*

private const val FAVOURITES_CHANGED_BROADCAST = "favouritesChangedBroadcast"
private const val BROADCAST_PASS_ID = "passId"
private const val BROADCAST_PASS_STATUS = "passStatus"
private const val GAME_ID_LABEL = "gameIdLabel"
private const val GRID_SPAN_COUNT = 2
class FavouritesActivity: AppCompatActivity() {
    private lateinit var favouritesCompositeDisposable: CompositeDisposable
    private lateinit var favouritesAdapter: GamesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_layout)
        buildRecyclerView()
        loadFavouriteGames()
    }

    private fun buildRecyclerView(){
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, GRID_SPAN_COUNT)
        favourites_list.apply { layoutManager = mLayoutManager }
        favouritesAdapter = GamesListAdapter()
        favourites_list.adapter = favouritesAdapter
        favouritesCompositeDisposable = CompositeDisposable()

        favouritesAdapter.setOnItemClickListener(object : GameClickListener {
            override fun onRecyclerItemPressed(position: Int) {
                val intent = Intent(this@FavouritesActivity, GameDetailsActivity::class.java)
                val itemId = favouritesAdapter.games[position].id - 1
                intent.putExtra(GAME_ID_LABEL, itemId)
                finish()
                startActivity(intent)
            }

            override fun onFavouriteClick(position: Int, status: Boolean) {
                sendBroadCastWithId(position, status)
                val changeStatusDisposable = changeFavouriteStatus(this@FavouritesActivity, position, status)
                changeStatusDisposable.addTo(favouritesCompositeDisposable)
            }
        })
    }

    private fun loadFavouriteGames(){
        val favouritesObservable = Observable.fromCallable { GamesDatabase.loadInstance(this).gamesDao().getFavouriteList() }
        val disposable = favouritesObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext{showProgressBar()}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                hideProgressBar()
                showData(it)
            }
        disposable.addTo(favouritesCompositeDisposable)
    }

    private fun sendBroadCastWithId(gameId: Int, status: Boolean){
        val broadCastIntent = Intent(FAVOURITES_CHANGED_BROADCAST)
        broadCastIntent.putExtra(BROADCAST_PASS_ID, gameId)
        broadCastIntent.putExtra(BROADCAST_PASS_STATUS, status)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadCastIntent)
    }

    private fun showData(list: List<GamesModel>){
        if (list.isNotEmpty()){
            favouritesAdapter.games = list
        }else{
            showMessage()
        }
    }

    private fun showProgressBar(){
        favourites_pb.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        favourites_pb.visibility = View.GONE
    }

    private fun showMessage(){
        favourite_message.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        favouritesCompositeDisposable.dispose()
    }
}