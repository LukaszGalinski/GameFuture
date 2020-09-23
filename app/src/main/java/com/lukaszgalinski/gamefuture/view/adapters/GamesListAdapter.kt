package com.lukaszgalinski.gamefuture.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.models.GamesModel
import com.lukaszgalinski.gamefuture.repositories.network.SERVER_DATA_URL
import com.lukaszgalinski.gamefuture.view.callbacks.GameClickListener
import kotlinx.android.synthetic.main.menu_list_row.view.*

class GamesListAdapter(private val context: Context) : RecyclerView.Adapter<GamesListAdapter.GamesViewHolder>() {
    private lateinit var gameClickListener: GameClickListener

    var games : List<GamesModel> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun setOnItemClickListener(itemClickListener: GameClickListener){
        this.gameClickListener = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        return GamesViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.menu_list_row,
                parent,
                false
            )
        )
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
        Glide.with(context).load(SERVER_DATA_URL + games[position].photo+".jpg").into(holder.image)
        holder.image.clipToOutline = true
        holder.favourite.isChecked = games[position].favourite
        holder.itemView.setOnClickListener {
            gameClickListener.onRecyclerItemPressed(position)
        }
        holder.favourite.setOnClickListener{
            gameClickListener.onFavouriteClick(games[position].gameId, holder.favourite.isChecked, position)
        }
    }
}