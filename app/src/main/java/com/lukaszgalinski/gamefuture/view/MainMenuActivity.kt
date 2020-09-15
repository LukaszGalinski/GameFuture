package com.lukaszgalinski.gamefuture.view

import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.databinding.AlertLeaveBinding
import com.lukaszgalinski.gamefuture.databinding.MainMenuGameAlertBinding
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.utilities.decodeImage
import com.lukaszgalinski.gamefuture.view.adapters.GamesListAdapter
import com.lukaszgalinski.gamefuture.view.callbacks.GameClickListener
import com.lukaszgalinski.gamefuture.viewmodels.MainMenuViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

private const val FAVOURITES_CHANGED_BROADCAST = "favouritesChangedBroadcast"
private const val BROADCAST_PASS_ID = "passId"
private const val BROADCAST_PASS_STATUS = "passStatus"
private const val ROOM_TAG = "Room: "
private const val GAME_ID_LABEL = "gameIdLabel"

class MainMenuActivity : SearchActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        compositeDisposable = CompositeDisposable()
        buildViewModel()
        buildRecyclerView()
        buildBottomNavigationBar()
    }

    private fun buildViewModel() {
        mMainMenuViewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)
        loadInitialData()
        mMainMenuViewModel.getGamesList()?.observe(this, Observer<List<GamesModel>> {
            gamesListAdapter.notifyDataSetChanged()
        })
    }

    private fun loadInitialData() {
        val favouritesObservable = Observable.fromCallable { mMainMenuViewModel.init(this) }
        val disposableFavourites =
            favouritesObservable.observeOn(AndroidSchedulers.mainThread()).doOnNext { showProgressBar() }.subscribeOn(Schedulers.io())
                .map { mMainMenuViewModel.getGamesList()?.value!! }.observeOn(AndroidSchedulers.mainThread()).subscribe {
                    hideProgressBar()
                    gamesListAdapter.games = it
                }
        compositeDisposable.add(disposableFavourites)
    }

    private fun buildRecyclerView() {
        gamesListAdapter = GamesListAdapter(this)
        binding.menuRecycler.adapter = gamesListAdapter
        gamesListAdapter.setOnItemClickListener(object : GameClickListener {
            override fun onRecyclerItemPressed(position: Int) {
                showAlertWithData(gamesListAdapter.games[position])
            }

            override fun onFavouriteClick(gameId: Int, status: Boolean, position: Int) {
                val favouriteChangeDisposable = mMainMenuViewModel.changeFavouriteStatus(this@MainMenuActivity, gameId, status, position)!!
                compositeDisposable.add(favouriteChangeDisposable)
            }
        })
    }

    private fun showAlertWithData(item: GamesModel) {
        val alertBinding = MainMenuGameAlertBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        setDialogFeatures(dialog)
        dialog.setContentView(alertBinding.root)
        dialog.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)


        alertBinding.rowImage.setImageBitmap(decodeImage(item.photo))
        alertBinding.rowImage.clipToOutline = true
        alertBinding.alertTitle.text = item.name
        alertBinding.alertDescription.text = item.description
        alertBinding.alertDescription.movementMethod = ScrollingMovementMethod()
        alertBinding.alertMoveForward.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, GameDetailsActivity::class.java)
            intent.putExtra(GAME_ID_LABEL, item.gameId)
            startActivity(intent)
        }
        dialog.show()
    }

    private fun setDialogFeatures(dialog: Dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun setFavouritesBroadcastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                val itemPosition = intent?.getIntExtra(BROADCAST_PASS_ID, 1)?.minus(1)!!
                val status = intent.getBooleanExtra(BROADCAST_PASS_STATUS, false)
                mMainMenuViewModel.updateRecyclerFavouriteStatus(itemPosition, status)
                gamesListAdapter.notifyItemChanged(itemPosition)
            }
        }
    }

    private fun buildBottomNavigationBar() {
        bottomNavigationView = binding.navigationBar.bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_btn -> {
                }
                R.id.update_btn -> {
                    forceDataUpdating()
                }
                R.id.favourites_btn -> {
                    startActivity(Intent(this, FavouritesActivity::class.java))
                }
                R.id.exit_btn -> {
                    showConfirmationAlert()
                }
            }
            true
        }
    }

    private fun showConfirmationAlert() {
        val bindingLeave = AlertLeaveBinding.inflate(layoutInflater)
        val alert = Dialog(this)
        setDialogFeatures(alert)
        alert.setContentView(bindingLeave.root)
        bindingLeave.confirm.setOnClickListener {
            alert.dismiss()
            finish()
        }
        bindingLeave.cancel.setOnClickListener {
            alert.dismiss()
            bottomNavigationView.selectedItemId = R.id.home_btn
        }

        alert.setOnCancelListener {
            bottomNavigationView.selectedItemId = R.id.home_btn
        }
        alert.show()
    }

    private fun forceDataUpdating() {
        showProgressBar()
        Toast.makeText(this, resources.getString(R.string.data_updating), Toast.LENGTH_SHORT).show()
        val forceUpdateDisposable = Observable.fromCallable { mMainMenuViewModel.forceDataUpdating(this) }.subscribeOn(Schedulers.io())
            .doOnError { Log.w(ROOM_TAG, ": " + "inserted") }.observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                bottomNavigationView.selectedItemId = R.id.home_btn
                hideProgressBar()
                Toast.makeText(this, resources.getString(R.string.data_updating_done), Toast.LENGTH_SHORT).show()
            }.map { gamesListAdapter.games = it }.subscribe()
        compositeDisposable.add(forceUpdateDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(setFavouritesBroadcastReceiver())
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.home_btn
        LocalBroadcastManager.getInstance(this).registerReceiver(setFavouritesBroadcastReceiver(), IntentFilter(FAVOURITES_CHANGED_BROADCAST))
    }
}