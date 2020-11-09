package com.lukaszgalinski.gamefuture.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.repositories.network.SERVER_DATA_URL

private const val IMAGE_LABEL = "image"

class GallerySlider : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.gallery_layout, container, false)
        val word = arguments?.getString(IMAGE_LABEL)
        val currentImage = view.findViewById<ImageView>(R.id.screens_gallery)
        Glide.with(requireContext())
            .load(SERVER_DATA_URL + word)
            .into(currentImage)
        currentImage.setBackgroundResource(R.drawable.border)
        currentImage.adjustViewBounds = true
        return view
    }

    companion object {
        fun newInstance(image: String): GallerySlider {
            val args = Bundle()
            args.putString(IMAGE_LABEL, image)
            val fragment = GallerySlider()
            fragment.arguments = args
            return fragment
        }
    }
}