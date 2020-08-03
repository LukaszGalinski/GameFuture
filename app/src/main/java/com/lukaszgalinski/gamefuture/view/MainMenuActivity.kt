package com.lukaszgalinski.gamefuture.view

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.lukaszgalinski.gamefuture.view.adapters.GamesListAdapter
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.repositories.database.changeFavouriteStatus
import com.lukaszgalinski.gamefuture.utilities.decodeImage
import com.lukaszgalinski.gamefuture.view.callbacks.GameClickListener
import com.lukaszgalinski.gamefuture.viewmodels.MainMenuViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_menu_layout.*
import java.util.concurrent.TimeUnit

private const val FAVOURITES_CHANGED_BROADCAST = "favouritesChangedBroadcast"
private const val BROADCAST_PASS_ID = "passId"
private const val BROADCAST_PASS_STATUS = "passStatus"
private const val FILTER_TIME = 1000L

class MainMenuActivity: SearchActivity() {
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var mMainMenuViewModel: MainMenuViewModel
    private lateinit var  gamesListAdapter : GamesListAdapter

    override fun onStart() {
        super.onStart()
        compositeDisposable = CompositeDisposable()
        mMainMenuViewModel = ViewModelProviders.of(this).get(MainMenuViewModel::class.java)
        val favouritesObservable = Observable.fromCallable { mMainMenuViewModel.init(this) }
        val disposableFavourites = favouritesObservable
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showProgressBar() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                hideProgressBar()
                gamesListAdapter.games = mMainMenuViewModel.getGamesList()?.value!!
            }

        mMainMenuViewModel.getGamesList()?.observe(this, Observer<List<GamesModel>> {
            gamesListAdapter.notifyDataSetChanged()
        })

        val textChangeListener = createTextChangeObservable()
        val textDisposable = textChangeListener
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showProgressBar() }
            .observeOn(Schedulers.io())
            .map { mMainMenuViewModel.filterData(it, this) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                hideProgressBar()
                showData(it)
            }
        compositeDisposable.add(textDisposable)
        compositeDisposable.add(disposableFavourites)
        buildRecyclerView()
        menu_favourites.setOnClickListener {
            startActivity(Intent(this, FavouritesActivity::class.java))
        }
    }

    private fun showData(data: MutableLiveData<List<GamesModel>>?) {
        if (data?.value?.isEmpty()!!){
            Toast.makeText(this, resources.getString(R.string.no_results), Toast.LENGTH_SHORT).show()
        }
        gamesListAdapter.games = data.value!!
    }

    private fun buildRecyclerView(){
        gamesListAdapter = GamesListAdapter()
        menu_recycler.adapter = gamesListAdapter

        gamesListAdapter.setOnItemClickListener(object :
            GameClickListener {
            override fun onRecyclerItemPressed(position: Int) {
                showAlertWithData(gamesList[position])
            }

            override fun onFavouriteClick(position: Int, status: Boolean) {
                val favouriteChangeDisposable =
                    changeFavouriteStatus(
                        this@MainMenuActivity,
                        position,
                        status
                    )
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
        imageView.setImageBitmap(
            decodeImage(
                item.photo
            )
        )
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

    private fun createTextChangeObservable(): Observable<String>{
        val textChangeObservable = Observable.create<String>{emitter ->
            val textChange = object:TextWatcher{
                override fun afterTextChanged(s: Editable?) = Unit
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.toString()?.let { emitter.onNext(it) }
                }
            }
            menuSearchBar.addTextChangedListener(textChange)
            emitter.setCancellable {
                menuSearchBar.removeTextChangedListener(textChange)
            }
        }
        return textChangeObservable.debounce(FILTER_TIME, TimeUnit.MILLISECONDS)
    }

    private fun setFavouritesBroadcastReceiver(): BroadcastReceiver{
        return object: BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val itemPosition = intent?.getIntExtra(BROADCAST_PASS_ID, 1)?.minus(1)!!
                val status = intent.getBooleanExtra(BROADCAST_PASS_STATUS, false)
                gamesList[itemPosition].favourite = status
                gamesListAdapter.notifyItemChanged(itemPosition)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(FAVOURITES_CHANGED_BROADCAST)
        LocalBroadcastManager.getInstance(this).registerReceiver(setFavouritesBroadcastReceiver(), intentFilter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(setFavouritesBroadcastReceiver())
    }
}