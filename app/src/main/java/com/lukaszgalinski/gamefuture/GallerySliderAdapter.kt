package com.lukaszgalinski.gamefuture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val IMAGE_LABEL = "image"
class GallerySliderAdapter: Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.gallery_layout, container, false)
        val word = arguments?.getString(IMAGE_LABEL)
        val textView = view.findViewById<TextView>(R.id.screens_gallery)
        textView.text = word
        return view
    }

    companion object {
        fun newInstance(image: String): GallerySliderAdapter{
            val args = Bundle()
            args.putString(IMAGE_LABEL, image )
            val fragment = GallerySliderAdapter()
            fragment.arguments = args
            return fragment
        }
    }
}

class GallerySlider(fragmentActivity: FragmentActivity , private val list: List<String>): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return GallerySliderAdapter.newInstance(list[position])
    }
}