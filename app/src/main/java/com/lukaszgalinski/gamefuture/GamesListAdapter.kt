package com.lukaszgalinski.gamefuture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.menu_list_row.view.*

private const val OFFSET_VALUE = 0
class GamesListAdapter(private val context: Context, private val gamesList: List<GamesData?>) : RecyclerView.Adapter<GamesListAdapter.GamesViewHolder>() {

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
        holder.image.setImageBitmap(decodeImage(gamesList[position]?.photoUrl))
    }

    private fun decodeImage(photoUrl: String?): Bitmap{
        val imageBytes = Base64.decode(photoUrl, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, OFFSET_VALUE, imageBytes.size)
    }
}