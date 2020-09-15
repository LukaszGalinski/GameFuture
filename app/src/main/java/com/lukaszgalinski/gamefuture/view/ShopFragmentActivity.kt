package com.lukaszgalinski.gamefuture.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukaszgalinski.gamefuture.databinding.ShopFragmentLayoutBinding
import com.lukaszgalinski.gamefuture.models.ShopPricesModel
import com.lukaszgalinski.gamefuture.view.adapters.ShopPricesAdapter
import com.lukaszgalinski.gamefuture.viewmodels.GameDetailsViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

private const val PRICES_LOADING_TAG = "Prices Loading: "
private var pricesData = emptyList<String?>()
private lateinit var pricesDisposable: Disposable
private lateinit var shopBinding: ShopFragmentLayoutBinding

class ShopFragmentActivity : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        shopBinding = ShopFragmentLayoutBinding.inflate(inflater)
        return shopBinding.root
    }

    companion object {
        fun newInstance(): ShopFragmentActivity {
            return ShopFragmentActivity()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModelProvider = ViewModelProvider(requireActivity()).get(GameDetailsViewModel::class.java)
        buildShopsRecycler()
        //hard coded to change on json loading
        val shopLinks = ShopPricesModel(
            "https://www.ultima.pl/ct/playstation-4/gry/role-playing-rpg/sekiro-shadows-die-twice-1",
            "https://www.morele.net/sekiro-shadows-die-twice-4141985/?utm_source=ceneo&utm_medium=referral",
            "https://www.e-key.eu/pl/sekiro-shadows-die-twice.html?utm_source=ceneo&utm_medium=referral"
        )
        loadTheData(viewModelProvider, shopLinks)
    }

    private fun buildShopsRecycler() {
        val pricesList = emptyList<String>()
        val pricesAdapter = context?.let { ShopPricesAdapter(it, pricesList) }
        shopBinding.shopPricesRecycler.adapter = pricesAdapter
    }

    private fun loadTheData(vmProvider: GameDetailsViewModel, shopLinks: ShopPricesModel) {
        val loadPricesObservable = Observable.fromCallable { vmProvider.loadPrices(shopLinks) }
        pricesDisposable = loadPricesObservable.observeOn(AndroidSchedulers.mainThread()).doOnNext { showProgressBar() }.subscribeOn(Schedulers.io())
            .map { pricesData = it }.observeOn(AndroidSchedulers.mainThread()).doOnComplete {
                shopBinding.shopPricesRecycler.adapter = context?.let { ShopPricesAdapter(it, pricesData) }
                hideProgressBar()
            }.doOnError {
                Log.w(PRICES_LOADING_TAG, "Error")
            }.subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        pricesDisposable.dispose()
    }

    private fun showProgressBar() {
        shopBinding.shopsProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        shopBinding.shopsProgressBar.visibility = View.GONE
    }
}