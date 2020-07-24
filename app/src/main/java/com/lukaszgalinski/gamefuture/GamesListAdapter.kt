package com.lukaszgalinski.gamefuture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lukaszgalinski.gamefuture.database.Games
import kotlinx.android.synthetic.main.menu_list_row.view.*

class GamesListAdapter : RecyclerView.Adapter<GamesListAdapter.GamesViewHolder>() {
    private lateinit var gameClickListener: GameClickListener

    var games: List<Games> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setOnItemClickListener(itemClickListener: GameClickListener){
        this.gameClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        return GamesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.menu_list_row, parent, false))
    }

    class GamesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.text1
        val image: ImageView = itemView.row_image
        val favourite: CheckBox = itemView.favourite
    }

    override fun getItemCount(): Int {
        return games.size
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        val element = games[position]
        holder.name.text = element.name
        holder.image.setImageBitmap(decodeImage(element.photo))
        holder.image.clipToOutline = true
        holder.favourite.isChecked = games[position].favourite
        holder.itemView.setOnClickListener {
            gameClickListener.onRecyclerItemPressed(position)
        }
        holder.favourite.setOnClickListener{
            gameClickListener.onFavouriteClick(position.inc(), holder.favourite.isChecked)
        }
    }
}