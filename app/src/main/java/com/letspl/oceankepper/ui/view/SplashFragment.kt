package com.letspl.oceankepper.ui.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.ActivityBaseBinding
import com.letspl.oceankepper.databinding.FragmentSplashBinding
import timber.log.Timber

class SplashFragment: Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding: FragmentSplashBinding get() = _binding!!
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = FragmentSplashBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.e("asdasd")

        // splash alpha animation
        binding.splashTv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.splash_anim))

        Handler(Looper.myLooper()!!).postDelayed({
//            activity.onReplaceFragment(Login, false)
        }, 3000)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}