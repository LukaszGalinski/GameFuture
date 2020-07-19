package com.lukaszgalinski.gamefuture.cardsfragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val PAGES_COUNT = 3
class FragmentsAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() =
        PAGES_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DescriptionFragmentActivity.newInstance()
            1 -> VideoFragmentActivity.newInstance()
            2 -> ShopFragmentActivity.newInstance()
            else -> DescriptionFragmentActivity.newInstance()
        }
    }
}