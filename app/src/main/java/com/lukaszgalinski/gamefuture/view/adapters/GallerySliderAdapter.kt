package com.lukaszgalinski.gamefuture.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lukaszgalinski.gamefuture.view.GallerySlider

class GallerySliderAdapter(fragmentActivity: FragmentActivity, private val list: List<String>) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return GallerySlider.newInstance(list[position])
    }
}