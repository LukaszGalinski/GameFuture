package com.lukaszgalinski.gamefuture.view

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.repositories.database.GamesDatabase
import io.reactivex.disposables.CompositeDisposable

abstract class SearchActivity: AppCompatActivity(){
    protected var gamesList = listOf<GamesModel>()
    private val compositeDisposable = CompositeDisposable()
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)
        progressBar = findViewById(R.id.games_progressBar)

    }

    override fun onDestroy() {
        super.onDestroy()
        GamesDatabase.destroyInstance()
        compositeDisposable.clear()
    }

    protected fun showProgressBar(){
        progressBar.visibility = View.VISIBLE
    }

    protected fun hideProgressBar(){
        progressBar.visibility = View.GONE
    }
}