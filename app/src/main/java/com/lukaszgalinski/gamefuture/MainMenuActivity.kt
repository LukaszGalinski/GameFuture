package com.lukaszgalinski.gamefuture

import android.text.Editable
import android.text.TextWatcher
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_menu_layout.*
import java.util.concurrent.TimeUnit

private const val FILTER_SIGNS_MINIMUM_VALUE = 3
private const val FILTER_TIME = 2000L
class MainMenuActivity: SearchActivity() {
    private lateinit var disposable: Disposable

    override fun onStart() {
        super.onStart()
        val textChangeListener = createTextChangeObservable()
        disposable = textChangeListener
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { showProgressBar() }
            .observeOn(Schedulers.io())
            .map { searchEngine.search(it)!! }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                hideProgressBar()
                showData(it)
            }
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
        return textChangeObservable.filter{it.length >= FILTER_SIGNS_MINIMUM_VALUE}.debounce(
            FILTER_TIME, TimeUnit.MILLISECONDS)
    }

    override fun onStop() {
        super.onStop()
        if (!disposable.isDisposed){
            disposable.dispose()
        }
    }
}