package com.lukaszgalinski.gamefuture

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.menu_list_row.view.*

class GamesListAdapter(private val context: Context, private val gamesList: List<GamesData?>) : RecyclerView.Adapter<GamesListAdapter.GamesViewHolder>() {
    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(itemClickListener: OnItemClickListener){
        this.onItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        return GamesViewHolder(LayoutInflater.from(context).inflate(R.layout.menu_list_row, parent, false))
    }

    class GamesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.text1
        val image: ImageView = itemView.row_image

    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        holder.name.text = gamesList[position]?.name
        holder.name.setShadowLayer(20f, 5f, 5f, Color.BLACK)
        holder.image.setImageBitmap(decodeImage(gamesList[position]?.photoUrl))
        holder.image.clipToOutline = true

        holder.itemView.setOnClickListener {
            onItemClickListener.onRecyclerItemPressed(position)
        }
    }
}