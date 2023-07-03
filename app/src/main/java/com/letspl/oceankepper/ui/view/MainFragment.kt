package com.letspl.oceankepper.ui.view

import android.graphics.Paint.Join
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.letspl.oceankepper.data.model.LoginModel
import com.letspl.oceankepper.databinding.FragmentLoginBinding
import com.letspl.oceankepper.databinding.FragmentMainBinding
import com.letspl.oceankepper.ui.viewmodel.LoginViewModel
import com.letspl.oceankepper.util.ContextUtil
import com.letspl.oceankepper.util.loginManager.AppleLoginManager
import com.letspl.oceankepper.util.loginManager.KakaoLoginManager
import com.letspl.oceankepper.util.loginManager.NaverLoginManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment: Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentMainBinding.inflate(layoutInflater)
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

        setUpViewModelObservers()
    }

    // viewModel 옵저버 세팅
    private fun setUpViewModelObservers() {

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onBackPressed() {
        activity.finish()
    }
}