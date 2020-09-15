package com.lukaszgalinski.gamefuture.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lukaszgalinski.gamefuture.R
import kotlinx.android.synthetic.main.shop_fragment_adapter.view.*

class ShopPricesAdapter(private val context: Context, private val pricesList: List<String?>) :
    RecyclerView.Adapter<ShopPricesAdapter.ShopPricesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopPricesViewHolder {
        return ShopPricesViewHolder(LayoutInflater.from(context).inflate(R.layout.shop_fragment_adapter, parent, false))
    }

    override fun getItemCount(): Int {
        return pricesList.size
    }

    override fun onBindViewHolder(holder: ShopPricesViewHolder, position: Int) {
        holder.price.text = pricesList[position]
        holder.shopLogo.background = ContextCompat.getDrawable(context, setShopImage(position))
        holder.shopLogo.adjustViewBounds = true
    }

    class ShopPricesViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val price: TextView = item.shop_item_price
        val shopLogo: ImageView = item.shop_logo
    }

    private fun setShopImage(position: Int): Int {
        return when (position) {
            0 -> R.drawable.ultima
            1 -> R.drawable.morele
            2 -> R.drawable.ekey_logo
            else -> R.drawable.no_image
        }
    }
}