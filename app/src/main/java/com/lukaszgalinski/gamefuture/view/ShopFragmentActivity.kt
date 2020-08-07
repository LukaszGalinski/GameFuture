package com.lukaszgalinski.gamefuture.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lukaszgalinski.gamefuture.R


class ShopFragmentActivity: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.shop_fragment_layout, container, false)
    }

    companion object {
        fun newInstance(): ShopFragmentActivity {
            println("nowa shop")
            return ShopFragmentActivity()
        }
    }
}