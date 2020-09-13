package com.lukaszgalinski.gamefuture.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.models.ShopPricesModel
import com.lukaszgalinski.gamefuture.view.adapters.ShopPricesAdapter
import com.lukaszgalinski.gamefuture.viewmodels.GameDetailsViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.shop_fragment_layout.*

private const val PRICES_LOADING_TAG = "Prices Loading: "
private var pricesData = emptyList<String?>()

class ShopFragmentActivity : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.shop_fragment_layout, container, false)
    }

    companion object {
        fun newInstance(): ShopFragmentActivity {
            return ShopFragmentActivity()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModelProvider = ViewModelProvider(requireActivity()).get(GameDetailsViewModel::class.java)
        //hard coded to change on json loading
        val shopLinks = ShopPricesModel(
            "https://www.ultima.pl/ct/playstation-4/gry/role-playing-rpg/sekiro-shadows-die-twice-1",
            "https://www.morele.net/sekiro-shadows-die-twice-4141985/?utm_source=ceneo&utm_medium=referral",
            "https://www.e-key.eu/pl/sekiro-shadows-die-twice.html?utm_source=ceneo&utm_medium=referral"
        )
        val pricesList = emptyList<String>()
        val pricesAdapter = context?.let { ShopPricesAdapter(it, pricesList) }
        shop_prices_recycler.adapter = pricesAdapter
        Observable.fromCallable {
            viewModelProvider.loadPrices(shopLinks)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map {
            pricesData = it
        }.doOnComplete {
            shop_prices_recycler.adapter = context?.let { ShopPricesAdapter(it, pricesData) }

        }.doOnError {
            Log.w(PRICES_LOADING_TAG, "Error")
        }.subscribe()
    }
}