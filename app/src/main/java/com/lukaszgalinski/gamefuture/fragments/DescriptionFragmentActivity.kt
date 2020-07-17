package com.lukaszgalinski.gamefuture.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lukaszgalinski.gamefuture.R

class DescriptionFragmentActivity : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.description_fragment_layout, container, false)
    }

    companion object {
        fun newInstance(): DescriptionFragmentActivity {
            return DescriptionFragmentActivity()
        }
    }
}