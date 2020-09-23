package com.lukaszgalinski.gamefuture.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.databinding.GameDetailsLayoutBinding
import com.lukaszgalinski.gamefuture.view.adapters.FragmentsAdapter
import com.lukaszgalinski.gamefuture.view.adapters.GallerySliderAdapter
import com.lukaszgalinski.gamefuture.viewmodels.GameDetailsViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.game_details_layout.*
import kotlinx.android.synthetic.main.video_fragment_layout.*
import kotlin.math.abs

private const val GAME_ID_LABEL = "gameIdLabel"
private const val SELECTED_IMAGE_SCALE_HEIGHT = .5f
private const val UNSELECTED_IMAGE_SCALE_HEIGHT = .25f
private const val MARGIN_PAGE_TRANSFORMER_VALUE = 20
private const val UNSELECTED_IMAGE_ALPHA = .3f
private const val SELECTED_IMAGE_ALPHA = 1f
lateinit var cardsPager: ViewPager2
private lateinit var cardsToolbar: TabLayout
private lateinit var galleryPager: ViewPager2
private lateinit var galleryToolbar: TabLayout
private lateinit var fragmentsAdapter: FragmentsAdapter
lateinit var detailsBinding: GameDetailsLayoutBinding

class GameDetailsActivity : FragmentActivity() {
    private lateinit var gameDetailsViewModel: GameDetailsViewModel
    private val galleryMaxVisibleElements = 3
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailsBinding = GameDetailsLayoutBinding.inflate(layoutInflater)
        setContentView(detailsBinding.root)
        val gameId = intent.extras?.getInt(GAME_ID_LABEL)
        println("zaladowane int: " + gameId)
        buildFragmentCards()
        gameDetailsViewModel = ViewModelProvider(this).get(GameDetailsViewModel::class.java)
        loadSingleData(gameId!!)

    }

    private fun loadSingleData(gameId: Int) {
        val singleElementLoadingObservable: Disposable = Single.fromCallable {
            gameDetailsViewModel.instance(this, gameId)
        }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
            .doOnSuccess {
                buildGallery()
                val tab = tabGallery.getTabAt(0)
                tab!!.select()
            }.subscribe()
        compositeDisposable.add(singleElementLoadingObservable)
    }

    private fun buildGallery() {
        galleryToolbar = detailsBinding.tabGallery
        galleryPager = detailsBinding.gallery
        val galleryImagesList = gameDetailsViewModel.getGalleryImagesLinks()

        println("gallery: " + galleryImagesList)
        val galleryAdapter = GallerySliderAdapter(this, galleryImagesList!!)
        galleryPager.adapter = galleryAdapter
        galleryAdapter.notifyDataSetChanged()
        createViewPagerAnimation(galleryPager)
        buildGalleryToolbar(
            galleryPager, galleryToolbar, galleryImagesList
        )
    }

    private fun buildFragmentCards() {
        cardsPager = detailsBinding.gamesViewPager
        cardsToolbar = detailsBinding.tabLayout
        fragmentsAdapter = FragmentsAdapter(this)
        cardsPager.adapter = fragmentsAdapter
        TabLayoutMediator(cardsToolbar, cardsPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = resources.getString(R.string.details_description)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_text_fields_24, null)
                }
                1 -> {
                    tab.text = resources.getString(R.string.details_video)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.icon_camera, null)
                }
                2 -> {
                    tab.text = resources.getString(R.string.details_shop)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_shopping_basket_24, null)
                }
            }
        }.attach()
    }

    private fun buildGalleryToolbar(pager: ViewPager2, tabLayout: TabLayout, imagesArray: List<String>) {
        // val galleryBinding = GalleryLayoutBinding.inflate(layoutInflater)
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            val customBackgroundLayout = View.inflate(this, R.layout.gallery_layout, null)
            val imageBackground = customBackgroundLayout.findViewById<ImageView>(R.id.screens_gallery)
            Glide.with(applicationContext).load("https://raw.githubusercontent.com/LukaszGalinski/GameFuture/master/appServerFiles/Sekiro/gallery_one.jpg").into(imageBackground)
            imageBackground.scaleType = ImageView.ScaleType.FIT_XY
            tab.view.alpha = UNSELECTED_IMAGE_ALPHA
            tab.customView = customBackgroundLayout
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view?.alpha = UNSELECTED_IMAGE_ALPHA
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.view?.alpha = SELECTED_IMAGE_ALPHA
            }
        })
    }

    private fun createViewPagerAnimation(viewPager: ViewPager2) {
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(MARGIN_PAGE_TRANSFORMER_VALUE))
        compositePageTransformer.addTransformer { view: View, fl: Float ->
            val position = 1 - abs(fl)
            view.scaleY = SELECTED_IMAGE_SCALE_HEIGHT + position + UNSELECTED_IMAGE_SCALE_HEIGHT
        }
        viewPager.offscreenPageLimit = galleryMaxVisibleElements
        viewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPager.setPageTransformer(compositePageTransformer)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onBackPressed() {
        if (youtube_player.isFullScreen()) {
            youtube_player.exitFullScreen()
        } else {
            finish()
        }
    }
}