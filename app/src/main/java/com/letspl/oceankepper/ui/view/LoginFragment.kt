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
class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    @Inject lateinit var naverLoginManager: NaverLoginManager
    @Inject lateinit var kakaoLoginManager: KakaoLoginManager
    lateinit var appleLoginManager: AppleLoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        appleLoginManager = AppleLoginManager(requireActivity())

        binding.naverLoginManager = naverLoginManager
        binding.appleLoginManager = appleLoginManager
        binding.kakaoLoginManager = kakaoLoginManager
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
        Timber.e("loginFragment onViewCreated")

        setUpViewModelObservers()
    }

    // viewModel 옵저버 세팅
    private fun setUpViewModelObservers() {
        loginViewModel.onLoginResult.observe(viewLifecycleOwner){ isExistLoginInfo ->
            Timber.e("onLoginResult")
            if(isExistLoginInfo != null) {
                if (isExistLoginInfo) {
                    // 있으면 메인 페이지로 이동
                    activity.onReplaceFragment(ActivityRecruitFragment(), false, false)
                } else {
                    // 없으면 회원가입 진행
                    activity.onReplaceFragment(JoinFragment())
                }
            }
        }
    }

    override fun onDestroyView() {
        loginViewModel.clearLiveData()
        _binding = null
        super.onDestroyView()
    }
}