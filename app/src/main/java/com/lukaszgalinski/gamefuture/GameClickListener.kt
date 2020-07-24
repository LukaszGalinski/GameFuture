package com.lukaszgalinski.gamefuture

interface GameClickListener {
    fun onRecyclerItemPressed(position: Int)
    fun onFavouriteClick(position: Int, status: Boolean)
}