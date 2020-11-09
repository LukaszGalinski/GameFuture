package com.lukaszgalinski.gamefuture.tools

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.lukaszgalinski.gamefuture.repositories.network.SERVER_DATA_URL

class ApplicationTools {

    fun loadImageIntoImageView(context: Context, imageView: ImageView, url: String){
        Glide.with(context).load(SERVER_DATA_URL + url).into(imageView)
    }
}