package com.lukaszgalinski.gamefuture.database

import android.content.Context
import android.util.Log
import com.lukaszgalinski.gamefuture.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

private const val CHANGE_FAVOURITE_STATUS_TAG = "Favourite status change"
fun changeFavouriteStatus(context: Context, position: Int, status: Boolean): Disposable{
    return Observable.fromCallable {
        (GamesDatabase.loadInstance(context).gamesDao()
            .changeFavouriteStatus(position, status))
    }
        .subscribeOn(Schedulers.io())
        .doOnError {
            Log.w(
                CHANGE_FAVOURITE_STATUS_TAG,
                ": " + context.resources.getString(R.string.error)
            )
        }
        .doOnComplete {
            Log.w(
                CHANGE_FAVOURITE_STATUS_TAG,
                ": " + context.resources.getString(R.string.success)
            )
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe()
}