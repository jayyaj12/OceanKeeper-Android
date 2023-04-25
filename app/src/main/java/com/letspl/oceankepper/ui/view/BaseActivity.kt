package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.ActivityBaseBinding
import timber.log.Timber

class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    interface OnBackPressedListener {
        fun onBackPressed()
    }

    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments
        for (fragment in fragmentList) {
            if (fragment is OnBackPressedListener) {
                (fragment as OnBackPressedListener).onBackPressed()
                return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.e("asdasdads")
        setupSplashFragment()
    }

    // splash setUp
    private fun setupSplashFragment() {
        onReplaceFragment(SplashFragment(), false)
    }

    // fragment 변경
    fun onReplaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction().let {
            it.replace(R.id.fragment_container, fragment)
            if(addToBackStack) {
                it.addToBackStack(null)
            }
            it.commitAllowingStateLoss()
        }
    }
}