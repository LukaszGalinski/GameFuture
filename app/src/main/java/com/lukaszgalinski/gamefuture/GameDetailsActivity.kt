package com.lukaszgalinski.gamefuture

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lukaszgalinski.gamefuture.fragments.FragmentsAdapter

private lateinit var viewPager: ViewPager2
private lateinit var tabToolbar: TabLayout
private lateinit var fragmentsAdapter: FragmentsAdapter
class GameDetailsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_details_layout)
        viewPager = findViewById(R.id.games_viewPager)
        tabToolbar = findViewById(R.id.tabLayout)

        val images_list = listOf("Janek", "Marek", "Franek", "Darek")
        val adapter = GallerySlider(this, images_list )
        val gallery = findViewById<ViewPager2>(R.id.gallery)
        gallery.adapter = adapter

        fragmentsAdapter = FragmentsAdapter(this)
        viewPager.adapter = fragmentsAdapter
        connectToolbarWithPager(viewPager, tabToolbar)
    }

    private fun connectToolbarWithPager(pager: ViewPager2, tabLayout: TabLayout){
        TabLayoutMediator(tabLayout, pager){tab, position ->
            when (position){
                0 -> {
                    tab.text = resources.getString(R.string.details_description)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_text_fields_24, null)
                }
                1->{
                    tab.text = resources.getString(R.string.details_video)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_camera_24, null)
                }
                2->{
                    tab.text = resources.getString(R.string.details_shop)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_shopping_basket_24, null)
                }
            }
        }.attach()
    }
}