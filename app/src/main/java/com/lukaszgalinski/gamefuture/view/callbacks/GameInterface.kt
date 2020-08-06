package com.lukaszgalinski.gamefuture.view.callbacks

interface GameClickListener {
    fun onRecyclerItemPressed(position: Int)
    fun onFavouriteClick(gameId: Int, status: Boolean, position: Int)
}



