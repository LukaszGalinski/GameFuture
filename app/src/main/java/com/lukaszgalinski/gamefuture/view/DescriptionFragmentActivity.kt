package com.lukaszgalinski.gamefuture.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.viewmodels.GameDetailsViewModel
import kotlinx.android.synthetic.main.description_fragment_layout.*

class DescriptionFragmentActivity : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.description_fragment_layout, container, false)
    }

    companion object {
        fun newInstance(): DescriptionFragmentActivity {
            return DescriptionFragmentActivity()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gameDetailsViewModel = ViewModelProvider(requireActivity()).get(GameDetailsViewModel::class.java)
        val gameItem = gameDetailsViewModel.getData()
        description_name.text = gameItem?.name
    }
}