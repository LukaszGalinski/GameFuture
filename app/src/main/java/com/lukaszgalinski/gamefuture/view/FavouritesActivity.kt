package com.lukaszgalinski.gamefuture.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lukaszgalinski.gamefuture.view.callbacks.GameClickListener
import com.lukaszgalinski.gamefuture.view.adapters.GamesListAdapter
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.viewmodels.FavouritesViewModel
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

class FavouritesActivity: AppCompatActivity() {
    private lateinit var favouritesCompositeDisposable: CompositeDisposable
    private lateinit var favouritesAdapter: GamesListAdapter
    private lateinit var favouritesViewModel: FavouritesViewModel
    private lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_layout)
        favouritesCompositeDisposable = CompositeDisposable()
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        favouritesViewModel = ViewModelProvider(this).get(FavouritesViewModel::class.java)
        loadFavouriteGames()

        favouritesViewModel.getGames()?.observe(this, Observer<List<GamesModel>>{
            favouritesAdapter.games = it
        })
        buildRecyclerView()
        loadFavouriteGames()
    }

    private fun buildRecyclerView(){
        favouritesAdapter = GamesListAdapter(this)
        favourites_list.adapter = favouritesAdapter
        favouritesAdapter.setOnItemClickListener(object : GameClickListener {
            override fun onRecyclerItemPressed(position: Int) {
                val intent = Intent(this@FavouritesActivity, GameDetailsActivity::class.java)
                val itemId = favouritesAdapter.games[position].id - 1
                intent.putExtra(GAME_ID_LABEL, itemId)
                finish()
                startActivity(intent)
            }

            override fun onFavouriteClick(gameId: Int, status: Boolean, position: Int ) {
                sendBroadCastWithId(gameId, status)
                val changeStatusDisposable = favouritesViewModel.changeStatus(this@FavouritesActivity, gameId, status, position)
                changeStatusDisposable?.addTo(favouritesCompositeDisposable)
            }
        })
    }

    private fun loadFavouriteGames(){
        val favouritesObservable = Observable.fromCallable {
            favouritesViewModel.init(this)
        }
        val disposable = favouritesObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext{showProgressBar()}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                hideProgressBar()
                showData()
            }
        disposable.addTo(favouritesCompositeDisposable)
    }

    private fun sendBroadCastWithId(gameId: Int, status: Boolean){
        val broadCastIntent = Intent(FAVOURITES_CHANGED_BROADCAST)
        broadCastIntent.putExtra(BROADCAST_PASS_ID, gameId)
        broadCastIntent.putExtra(BROADCAST_PASS_STATUS, status)
        localBroadcastManager.sendBroadcast(broadCastIntent)
    }

    private fun showData(){
        val favouriteData = favouritesViewModel.getGames()?.value!!
        if (favouriteData.isEmpty()){
            showMessage()
        }
        favouritesAdapter.games = favouriteData

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

    override fun onPause() {
        super.onPause()
        favouritesCompositeDisposable.dispose()
    }
}