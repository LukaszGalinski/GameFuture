package com.lukaszgalinski.gamefuture

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lukaszgalinski.gamefuture.cardsfragments.FragmentsAdapter
import kotlin.math.abs

private const val SELECTED_IMAGE_SCALE_HEIGHT = .5f
private const val UNSELECTED_IMAGE_SCALE_HEIGHT = .25f
private const val MARGIN_PAGE_TRANSFORMER_VALUE = 20
private const val UNSELECTED_IMAGE_ALPHA = .3f
private const val SELECTED_IMAGE_ALPHA = 1f
private lateinit var cardsPager: ViewPager2
private lateinit var cardsToolbar: TabLayout
private lateinit var fragmentsAdapter: FragmentsAdapter

class GameDetailsActivity : FragmentActivity() {

    private val visibleGalleryImagesCount = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_details_layout)
        cardsPager = findViewById(R.id.games_viewPager)
        cardsToolbar = findViewById(R.id.tabLayout)
        val galleryToolbar = findViewById<TabLayout>(R.id.tabGallery)
        val galleryPager = findViewById<ViewPager2>(R.id.gallery)
        val galleryImagesList = arrayListOf(R.drawable.acoddysey, R.drawable.acwallhalla, R.drawable.cabal)
        val galleryAdapter = GallerySliderAdapter(this, galleryImagesList)
        galleryPager.adapter = galleryAdapter
        createViewPagerAnimation(galleryPager)
        setUnselectedGalleryItems(galleryToolbar)
        setImagesGalleryToolbar(galleryPager, galleryToolbar, galleryImagesList)
        fragmentsAdapter = FragmentsAdapter(this)
        cardsPager.adapter = fragmentsAdapter
        setCardsToolbar(cardsPager, cardsToolbar)
    }

    private fun setCardsToolbar(pager: ViewPager2, tabLayout: TabLayout) {
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = resources.getString(R.string.details_description)
                    tab.icon = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_text_fields_24,
                        null
                    )
                }
                1 -> {
                    tab.text = resources.getString(R.string.details_video)
                    tab.icon = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.icon_camera,
                        null
                    )
                }
                2 -> {
                    tab.text = resources.getString(R.string.details_shop)
                    tab.icon = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_shopping_basket_24,
                        null
                    )
                }
            }
        }.attach()
    }

    private fun setImagesGalleryToolbar(pager: ViewPager2, tabLayout: TabLayout, imagesArray: ArrayList<Int>) {
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            val customBackgroundLayout =
                View.inflate(this, R.layout.gallery_layout, null)
            val imageBackground =
                customBackgroundLayout.findViewById<ImageView>(R.id.screens_gallery)
            imageBackground.setImageResource(imagesArray[position])
            imageBackground.scaleType = ImageView.ScaleType.FIT_XY
            tab.view.alpha = UNSELECTED_IMAGE_ALPHA
            tab.customView = customBackgroundLayout
        }.attach()

    }

    private fun setUnselectedGalleryItems(toolbar: TabLayout) {
        toolbar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) { tab?.view?.alpha = UNSELECTED_IMAGE_ALPHA }
            override fun onTabSelected(tab: TabLayout.Tab?) { tab?.view?.alpha = SELECTED_IMAGE_ALPHA }
        })
    }

    private fun createViewPagerAnimation(viewPager: ViewPager2) {
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(MARGIN_PAGE_TRANSFORMER_VALUE))
        compositePageTransformer.addTransformer { view: View, fl: Float ->
            val position = 1 - abs(fl)
            view.scaleY = SELECTED_IMAGE_SCALE_HEIGHT + position + UNSELECTED_IMAGE_SCALE_HEIGHT
        }
        viewPager.offscreenPageLimit = visibleGalleryImagesCount
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPager.setPageTransformer(compositePageTransformer)
    }
}