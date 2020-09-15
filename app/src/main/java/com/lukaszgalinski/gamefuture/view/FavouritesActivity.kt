package com.lukaszgalinski.gamefuture.view

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lukaszgalinski.gamefuture.view.callbacks.GameClickListener
import com.lukaszgalinski.gamefuture.view.adapters.GamesListAdapter
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.databinding.AlertLeaveBinding
import com.lukaszgalinski.gamefuture.databinding.FavouritesLayoutBinding
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
private const val ROOM_TAG = "Room: "

class FavouritesActivity : AppCompatActivity() {
    private lateinit var favouritesCompositeDisposable: CompositeDisposable
    private lateinit var favouritesAdapter: GamesListAdapter
    private lateinit var favouritesViewModel: FavouritesViewModel
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bindingFavourites: FavouritesLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favouritesCompositeDisposable = CompositeDisposable()
        bindingFavourites = FavouritesLayoutBinding.inflate(layoutInflater)
        setContentView(bindingFavourites.root)
        buildViewModel()
        buildRecyclerView()
        loadFavouriteGames()
        buildBottomNavigationBar()
    }

    private fun buildViewModel() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        favouritesViewModel = ViewModelProvider(this).get(FavouritesViewModel::class.java)
        loadFavouriteGames()
        favouritesViewModel.getGames()?.observe(this, Observer<List<GamesModel>> {
            favouritesAdapter.games = it
        })
    }

    private fun buildRecyclerView() {
        favouritesAdapter = GamesListAdapter(this)
        favourites_list.adapter = favouritesAdapter
        favouritesAdapter.setOnItemClickListener(object : GameClickListener {
            override fun onRecyclerItemPressed(position: Int) {
                val intent = Intent(this@FavouritesActivity, GameDetailsActivity::class.java)
                val itemId = favouritesAdapter.games[position].gameId - 1
                intent.putExtra(GAME_ID_LABEL, itemId)
                finish()
                startActivity(intent)
            }

            override fun onFavouriteClick(gameId: Int, status: Boolean, position: Int) {
                sendBroadCastWithId(gameId, status)
                val changeStatusDisposable = favouritesViewModel.changeStatus(this@FavouritesActivity, gameId, status, position)
                changeStatusDisposable?.addTo(favouritesCompositeDisposable)
            }
        })
    }

    private fun loadFavouriteGames() {
        val favouritesObservable = Observable.fromCallable { favouritesViewModel.init(this) }
        val disposable = favouritesObservable.observeOn(AndroidSchedulers.mainThread()).doOnNext { showProgressBar() }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                hideProgressBar()
                showData()
            }
        disposable.addTo(favouritesCompositeDisposable)
    }

    private fun sendBroadCastWithId(gameId: Int, status: Boolean) {
        val broadCastIntent = Intent(FAVOURITES_CHANGED_BROADCAST)
        broadCastIntent.putExtra(BROADCAST_PASS_ID, gameId)
        broadCastIntent.putExtra(BROADCAST_PASS_STATUS, status)
        localBroadcastManager.sendBroadcast(broadCastIntent)
    }

    private fun showData() {
        val favouriteData = favouritesViewModel.getGames()?.value!!
        if (favouriteData.isEmpty()) {
            showMessage()
        }
        favouritesAdapter.games = favouriteData
    }

    private fun buildBottomNavigationBar() {
        bottomNavigationView = bindingFavourites.navigationBar.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.favourites_btn
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_btn -> {
                    finish()
                }
                R.id.update_btn -> {
                    forceDataUpdating()
                }
                R.id.favourites_btn -> {
                }
                R.id.exit_btn -> {
                    showConfirmationAlert()
                }
            }
            true
        }
    }

    private fun showConfirmationAlert() {
        val alertBinding = AlertLeaveBinding.inflate(layoutInflater)
        val alert = Dialog(this)
        setAlertFeatures(alert)
        alert.setContentView(alertBinding.root)
        alertBinding.confirm.setOnClickListener {
            alert.dismiss()
            finishAffinity()
        }
        alertBinding.cancel.setOnClickListener {
            alert.dismiss()
            bottomNavigationView.selectedItemId = R.id.favourites_btn
        }
        alert.setOnCancelListener { bottomNavigationView.selectedItemId = R.id.favourites_btn }
        alert.show()
    }

    private fun setAlertFeatures(alert: Dialog) {
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alert.setCanceledOnTouchOutside(true)
        alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun forceDataUpdating() {
        showProgressBar()
        Toast.makeText(this, resources.getString(R.string.data_updating), Toast.LENGTH_SHORT).show()
        val forceUpdateDisposable = Observable.fromCallable { favouritesViewModel.forceDataUpdating(this) }.subscribeOn(Schedulers.io())
            .doOnError { Log.w(ROOM_TAG, ": " + "inserted") }.observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                bottomNavigationView.selectedItemId = R.id.home_btn
                hideProgressBar()
                Toast.makeText(this, resources.getString(R.string.data_updating_done), Toast.LENGTH_SHORT).show()
            }.subscribe()
        favouritesCompositeDisposable.add(forceUpdateDisposable)
    }

    private fun showProgressBar() {
        bindingFavourites.favouritesPb.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        bindingFavourites.favouritesPb.visibility = View.GONE
    }

    private fun showMessage() {
        bindingFavourites.favouriteMessage.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        favouritesCompositeDisposable.dispose()
    }
}