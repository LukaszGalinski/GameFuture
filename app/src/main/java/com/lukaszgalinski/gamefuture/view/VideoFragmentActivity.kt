package com.lukaszgalinski.gamefuture.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.databinding.VideoFragmentLayoutBinding
import com.lukaszgalinski.gamefuture.viewmodels.GameDetailsViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class VideoFragmentActivity : Fragment() {
    lateinit var youTubePlayer: YouTubePlayerView
    private lateinit var videoBinding: VideoFragmentLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        videoBinding = VideoFragmentLayoutBinding.inflate(inflater, container, false)
        return videoBinding.root
    }

    companion object {
        fun newInstance(): VideoFragmentActivity {
            return VideoFragmentActivity()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gameDetailsViewModel = ViewModelProvider(requireActivity()).get(GameDetailsViewModel::class.java)
        val gameItem = gameDetailsViewModel.getData()
        buildYouTubeVideo(gameItem?.videoId!!)
    }

    private fun buildYouTubeVideo(videoURL: String) {
        youTubePlayer = videoBinding.youtubePlayer
        viewLifecycleOwner.lifecycle.addObserver(youTubePlayer)

        youTubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(videoURL, 0F)
            }
        })
        youTubePlayer.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                val mainLayout: ConstraintLayout = detailsBinding.rootView
                createFullScreenViewPager(mainLayout)
                hideScreenWidgets()
            }

            override fun onYouTubePlayerExitFullScreen() {
                val mainLayout: ConstraintLayout = detailsBinding.rootView
                showScreenWidgets()
                hideFullScreenViewPager(mainLayout)
            }
        })
    }

    private fun createConstraints(mConstraintLayout: ConstraintLayout): ConstraintSet {
        val mConstraintSet = ConstraintSet()
        mConstraintSet.clone(mConstraintLayout)
        return mConstraintSet
    }

    private fun hideScreenWidgets() {
        detailsBinding.tabGallery.visibility = View.GONE
        detailsBinding.gallery.visibility = View.GONE
        detailsBinding.tabLayout.visibility = View.GONE
    }

    private fun showScreenWidgets() {
        detailsBinding.tabGallery.visibility = View.VISIBLE
        detailsBinding.gallery.visibility = View.VISIBLE
        detailsBinding.tabLayout.visibility = View.VISIBLE
    }

    private fun createFullScreenViewPager(mConstraintLayout: ConstraintLayout) {
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        cardsPager.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        val mConstraintSet = createConstraints(mConstraintLayout)
        mConstraintSet.connect(
            R.id.games_viewPager, ConstraintSet.TOP, R.id.rootView, ConstraintSet.TOP
        )
        mConstraintSet.applyTo(mConstraintLayout)
    }

    private fun hideFullScreenViewPager(mainLayout: ConstraintLayout) {
        cardsPager.layoutParams.height = 0
        val mConstraintSet = createConstraints(mainLayout)
        mConstraintSet.connect(
            R.id.games_viewPager, ConstraintSet.TOP, R.id.tabLayout, ConstraintSet.BOTTOM
        )
        mConstraintSet.applyTo(mainLayout)
    }
}