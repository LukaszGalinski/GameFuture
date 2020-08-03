package com.lukaszgalinski.gamefuture.view.callbacks

interface GameClickListener {
    fun onRecyclerItemPressed(position: Int)
    fun onFavouriteClick(position: Int, status: Boolean)
}



