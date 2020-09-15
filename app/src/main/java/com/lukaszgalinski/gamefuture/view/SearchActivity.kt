package com.lukaszgalinski.gamefuture.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.lukaszgalinski.gamefuture.databinding.MainMenuLayoutBinding
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.view.adapters.GamesListAdapter
import com.lukaszgalinski.gamefuture.viewmodels.MainMenuViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_menu_layout.*
import java.util.concurrent.TimeUnit

private const val FILTER_TIME = 1000L
abstract class SearchActivity: AppCompatActivity() {
    protected lateinit var mMainMenuViewModel: MainMenuViewModel
    private val compositeDisposable = CompositeDisposable()
    private lateinit var progressBar: ProgressBar
    protected lateinit var  gamesListAdapter : GamesListAdapter
    protected lateinit var binding: MainMenuLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainMenuLayoutBinding.inflate(layoutInflater)
        createEditTextChangeDisposable()
        setContentView(binding.root)
    }

    private fun createEditTextChangeDisposable(){
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
    }

    private fun createTextChangeObservable(): Observable<String> {
        val textChangeObservable = Observable.create<String>{ emitter ->
            val textChange = object: TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    s?.toString()?.let { emitter.onNext(it) }
                }
            }
            binding.menuSearchBar.addTextChangedListener(textChange)
            emitter.setCancellable {
                menuSearchBar.removeTextChangedListener(textChange)
            }
        }
        return textChangeObservable.debounce(FILTER_TIME, TimeUnit.MILLISECONDS)
    }

    private fun showData(filteredData: LiveData<List<GamesModel>>?) {
        mMainMenuViewModel.showNoResults(this)
        gamesListAdapter.games = filteredData?.value!!
    }

    protected fun showProgressBar(){
        binding.gamesProgressBar.visibility = View.VISIBLE
    }

    protected fun hideProgressBar(){
        binding.gamesProgressBar.visibility = View.GONE
    }
}