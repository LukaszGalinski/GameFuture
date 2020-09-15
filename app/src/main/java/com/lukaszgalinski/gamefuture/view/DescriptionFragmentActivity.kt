package com.lukaszgalinski.gamefuture.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukaszgalinski.gamefuture.databinding.DescriptionFragmentLayoutBinding
import com.lukaszgalinski.gamefuture.viewmodels.GameDetailsViewModel

class DescriptionFragmentActivity : Fragment() {
    private lateinit var descriptionBinding: DescriptionFragmentLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        descriptionBinding = DescriptionFragmentLayoutBinding.inflate(inflater)
        return descriptionBinding.root
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
        descriptionBinding.descriptionName.text = gameItem?.name
    }
}