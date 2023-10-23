package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.dto.GuideItemDto
import com.letspl.oceankepper.databinding.FragmentGuideBinding
import com.letspl.oceankepper.databinding.FragmentGuideDetailBinding
import com.letspl.oceankepper.ui.adapter.GuideListAdapter
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import timber.log.Timber

class GuideDetailFragment(private val videoId: String) : Fragment(), BaseActivity.OnBackPressedListener {

    private var _binding: FragmentGuideDetailBinding? = null
    val binding: FragmentGuideDetailBinding get() = _binding!!

    private lateinit var guideListAdapter: GuideListAdapter
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onBackPressed() {
        onClickBackBtn()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuideDetailBinding.inflate(layoutInflater)
        binding.guideDetailFragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPlayYoutubeVideo()
    }

    private fun setupPlayYoutubeVideo() {
        lifecycle.addObserver(binding.youtubeVideoView)

        binding.youtubeVideoView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)

                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
    }

    fun onClickBackBtn() {
        activity.onReplaceFragment(GuideFragment(), false, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}