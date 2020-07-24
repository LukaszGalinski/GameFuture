package com.lukaszgalinski.gamefuture

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.lukaszgalinski.gamefuture.database.Games
import com.lukaszgalinski.gamefuture.database.GamesDatabase
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_menu_layout.*
import java.util.concurrent.TimeUnit

private const val FILTER_TIME = 1000L
private const val CHANGE_FAVOURITE_STATUS_TAG = "Favourite status change"
class MainMenuActivity: SearchActivity() {
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onStart() {
        super.onStart()
        val textChangeListener = createTextChangeObservable()
        compositeDisposable = CompositeDisposable()
        val disposable = textChangeListener
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showProgressBar() }
            .observeOn(Schedulers.io())
            .map { searchEngine.search(it)!! }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                hideProgressBar()
                showData(it)
            }
        compositeDisposable.add(disposable)

        gamesListAdapter.setOnItemClickListener(object : GameClickListener {
            override fun onRecyclerItemPressed(position: Int) {
                showAlertWithData(gamesList[position])
            }

            override fun onFavouriteClick(position: Int, status: Boolean){
                val favouriteChangeDisposable = Observable.fromCallable { (GamesDatabase.loadInstance(this@MainMenuActivity).gamesDao().changeFavouriteStatus(position, status)) }
                    .subscribeOn(Schedulers.io())
                    .doOnError{ Log.w(CHANGE_FAVOURITE_STATUS_TAG,": Error" ) }
                    .doOnComplete{Log.w(CHANGE_FAVOURITE_STATUS_TAG,": Success" )}
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
                compositeDisposable.add(favouriteChangeDisposable)
            }
        })
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

    override fun onStop() {
        super.onStop()
        if (compositeDisposable.isDisposed){
            compositeDisposable.dispose()
        }
    }
}