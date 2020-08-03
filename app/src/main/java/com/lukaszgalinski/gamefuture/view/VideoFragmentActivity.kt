package com.lukaszgalinski.gamefuture.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lukaszgalinski.gamefuture.R


class VideoFragmentActivity: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.video_fragment_layout, container, false)
    }

    companion object {
        fun newInstance(): VideoFragmentActivity {
            return VideoFragmentActivity()
        }
    }
}