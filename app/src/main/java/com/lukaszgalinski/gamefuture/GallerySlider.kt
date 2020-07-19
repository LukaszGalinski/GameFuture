package com.lukaszgalinski.gamefuture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val IMAGE_LABEL = "image"
class GallerySlider: Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.gallery_layout, container, false)
        val word = arguments?.getInt(IMAGE_LABEL)
        val currentImage = view.findViewById<ImageView>(R.id.screens_gallery)
        currentImage.setImageResource(word ?: R.drawable.acoddysey)
        currentImage.setBackgroundResource(R.drawable.border)
        return view
    }

    companion object {
        fun newInstance(image: Int): GallerySlider {
            val args = Bundle()
            args.putInt(IMAGE_LABEL, image)
            val fragment = GallerySlider()
            fragment.arguments = args
            return fragment
        }
    }
}

class GallerySliderAdapter(fragmentActivity: FragmentActivity , private val list: ArrayList<Int>): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return GallerySlider.newInstance(list[position])
    }
}