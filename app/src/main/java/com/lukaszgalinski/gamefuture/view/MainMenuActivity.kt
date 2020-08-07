package com.lukaszgalinski.gamefuture.view

import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.utilities.decodeImage
import com.lukaszgalinski.gamefuture.view.adapters.GamesListAdapter
import com.lukaszgalinski.gamefuture.view.callbacks.GameClickListener
import com.lukaszgalinski.gamefuture.viewmodels.MainMenuViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_menu_layout.*

private const val FAVOURITES_CHANGED_BROADCAST = "favouritesChangedBroadcast"
private const val BROADCAST_PASS_ID = "passId"
private const val BROADCAST_PASS_STATUS = "passStatus"

class MainMenuActivity: SearchActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)
        compositeDisposable = CompositeDisposable()
        mMainMenuViewModel = ViewModelProvider(this).get(MainMenuViewModel::class.java)
        loadInitialData()
        mMainMenuViewModel.getGamesList()?.observe(this, Observer<List<GamesModel>> {
            gamesListAdapter.notifyDataSetChanged()
        })
        buildRecyclerView()
        buildBottomNavigationBar()
    }

    private fun loadInitialData(){
        val favouritesObservable = Observable.fromCallable { mMainMenuViewModel.init(this) }
        val disposableFavourites = favouritesObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showProgressBar() }
            .subscribeOn(Schedulers.io())
            .map {  mMainMenuViewModel.getGamesList()?.value!! }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                hideProgressBar()
                gamesListAdapter.games = it

            }
        compositeDisposable.add(disposableFavourites)
    }

    private fun buildRecyclerView(){
        gamesListAdapter = GamesListAdapter(this)
        menu_recycler.adapter = gamesListAdapter
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
        }
        dialog.show()
    }

    private fun setFavouritesBroadcastReceiver(): BroadcastReceiver{
        return object: BroadcastReceiver(){
            override fun onReceive(context: Context, intent: Intent?) {
                val itemPosition = intent?.getIntExtra(BROADCAST_PASS_ID, 1)?.minus(1)!!
                val status = intent.getBooleanExtra(BROADCAST_PASS_STATUS, false)
                mMainMenuViewModel.updateRecyclerFavouriteStatus(itemPosition, status)
                gamesListAdapter.notifyItemChanged(itemPosition)
            }
        }
    }

    private fun buildBottomNavigationBar(){
        val parent: View = findViewById(R.id.navigation_bar)
        bottomNavigationView = parent.findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.home_btn -> {}
                R.id.update_btn -> {
                    mMainMenuViewModel.forceDataUpdating(this)
                    recreate()
                    Toast.makeText(this, resources.getString(R.string.data_updated), Toast.LENGTH_SHORT).show()}
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

    private fun showConfirmationAlert(){
        val alert = Dialog(this)
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alert.setCanceledOnTouchOutside(true)
        alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alert.setContentView(R.layout.alert_leave)
        val positiveButton = alert.findViewById<Button>(R.id.confirm)
        val negativeButton = alert.findViewById<Button>(R.id.cancel)
        positiveButton.setOnClickListener{
            alert.dismiss()
            finish()
        }
        negativeButton.setOnClickListener {
            alert.dismiss()
            bottomNavigationView.selectedItemId = R.id.home_btn
        }

        alert.setOnCancelListener {
            bottomNavigationView.selectedItemId = R.id.home_btn
        }
        alert.show()
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