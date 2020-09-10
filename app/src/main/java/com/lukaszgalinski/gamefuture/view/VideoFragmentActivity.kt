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
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.lukaszgalinski.gamefuture.R
import com.lukaszgalinski.gamefuture.viewmodels.GameDetailsViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

private const val YOUTUBE_VIDEO_ID = "FLjuoF5Si1U"

class VideoFragmentActivity : Fragment() {
    private lateinit var youTubePlayer: YouTubePlayerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.video_fragment_layout, container, false)
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
        buildYouTubeVideo(YOUTUBE_VIDEO_ID)
    }

    private fun buildYouTubeVideo(videoURL: String){
        youTubePlayer = view?.findViewById(R.id.youtube_player)!!
        viewLifecycleOwner.lifecycle.addObserver(youTubePlayer)

        youTubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(videoURL, 0F)
            }
        })

        youTubePlayer.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                val mConstraintLayout: ConstraintLayout = view!!.rootView.findViewById(R.id.rootView)
                hideScreenWidgets(mConstraintLayout)
                activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                val mConstraintSet = createConstraints(mConstraintLayout)
                mConstraintSet.connect(
                    R.id.games_viewPager, ConstraintSet.TOP, R.id.rootView, ConstraintSet.TOP
                )
                mConstraintSet.applyTo(mConstraintLayout)
            }

            override fun onYouTubePlayerExitFullScreen() {
                val mConstraintLayout: ConstraintLayout = view!!.rootView.findViewById(R.id.rootView)
                showScreenWidgets(mConstraintLayout)
                val mConstraintSet = createConstraints(mConstraintLayout)
                mConstraintSet.connect(
                    R.id.games_viewPager, ConstraintSet.TOP, R.id.tabLayout, ConstraintSet.BOTTOM
                );
                mConstraintSet.applyTo(mConstraintLayout)
            }
        })
    }

    private fun createConstraints(mConstraintLayout: ConstraintLayout): ConstraintSet{
        val mConstraintSet = ConstraintSet()
        mConstraintSet.clone(mConstraintLayout)
        return mConstraintSet
    }

    private fun hideScreenWidgets(mConstraintLayout: ConstraintLayout){
        mConstraintLayout.findViewById<TabLayout>(R.id.tabGallery).visibility = View.GONE
        mConstraintLayout.findViewById<ViewPager2>(R.id.gallery).visibility = View.GONE
        mConstraintLayout.findViewById<TabLayout>(R.id.tabLayout).visibility = View.GONE
    }

    private fun showScreenWidgets(mConstraintLayout: ConstraintLayout){
        mConstraintLayout.findViewById<TabLayout>(R.id.tabGallery).visibility = View.VISIBLE
        mConstraintLayout.findViewById<ViewPager2>(R.id.gallery).visibility = View.VISIBLE
        mConstraintLayout.findViewById<TabLayout>(R.id.tabLayout).visibility = View.VISIBLE
    }
}