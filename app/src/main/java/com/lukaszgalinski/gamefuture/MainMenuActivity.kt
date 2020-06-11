package com.lukaszgalinski.gamefuture

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_menu_layout.*

class MainMenuActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)
        val value = GamesData("numer", "nic", "http")
        val arrayList: ArrayList<GamesData> = ArrayList()
        arrayList.add(value)
        arrayList.add(value)
        arrayList.add(value)
        arrayList.add(value)
        arrayList.add(value)
        arrayList.add(value)
        arrayList.add(value)


        val adapter = GamesListAdapter(this, arrayList)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
        menu_recycler.adapter = adapter
        menu_recycler.apply {
            layoutManager = mLayoutManager
        }
    }
}