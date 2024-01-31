package com.letspl.oceankeeper.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.letspl.oceankeeper.databinding.FragmentLoginBinding
import com.letspl.oceankeeper.ui.viewmodel.LoginViewModel
import com.letspl.oceankeeper.util.loginManager.AppleLoginManager
import com.letspl.oceankeeper.util.loginManager.KakaoLoginManager
import com.letspl.oceankeeper.util.loginManager.NaverLoginManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    @Inject
    lateinit var naverLoginManager: NaverLoginManager

    @Inject
    lateinit var kakaoLoginManager: KakaoLoginManager
    lateinit var appleLoginManager: AppleLoginManager

    override fun onBackPressed() {
        activity.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        appleLoginManager = AppleLoginManager(requireActivity(), loginViewModel)

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
        setUpViewModelObservers()
    }

    // viewModel 옵저버 세팅
    private fun setUpViewModelObservers() {
        loginViewModel.errorMsg.observe(viewLifecycleOwner) {
            if (it != "") {
                activity.showErrorMsg(it)
            }
        }

        loginViewModel.onLoginResult.observe(viewLifecycleOwner) { isExistLoginInfo ->
            if (isExistLoginInfo != null) {
                if (isExistLoginInfo) {
                    // 있으면 메인 페이지로 이동
                    activity.onReplaceFragment(null, false, true, 1)
                } else {
                    // 없으면 회원가입 진행
                    activity.onReplaceFragment(JoinFragment())
                }
            }
        }
    }

    override fun onDestroyView() {
        loginViewModel.clearLiveData()
        loginViewModel.clearErrorMsg()
        _binding = null
        super.onDestroyView()
    }
}