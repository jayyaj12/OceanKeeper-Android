package com.letspl.oceankepper.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.letspl.oceankepper.databinding.FragmentJoinBinding
import com.letspl.oceankepper.databinding.FragmentLoginBinding
import com.letspl.oceankepper.ui.viewmodel.JoinViewModel
import com.letspl.oceankepper.ui.viewmodel.LoginViewModel
import com.letspl.oceankepper.util.BaseUrlType
import com.letspl.oceankepper.util.ContextUtil
import com.letspl.oceankepper.util.loginManager.NaverLoginManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class JoinFragment: Fragment() {
    private var _binding: FragmentJoinBinding? = null
    private val binding: FragmentJoinBinding get() = _binding!!
    @Inject lateinit var activity: BaseActivity
    @Inject lateinit var naverLoginManager: NaverLoginManager
    private val loginViewModel: LoginViewModel by viewModels()
    private val joinViewModel: JoinViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentJoinBinding.inflate(layoutInflater)
        binding.joinViewModel = joinViewModel
        binding.lifecycleOwner = this
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
        BaseUrlType.setBaseUrlType("naver")

        Glide.with(requireContext())
            .load(loginViewModel.getLoginInfo().profile)
            .into(binding.profileIv)
        binding.nicknameEt.setText(loginViewModel.getLoginInfo().nickname)
        joinViewModel.profileTempFileCreated.observe(viewLifecycleOwner) {
            joinViewModel.uploadImageFile(it)
        }

    }

    private fun setUpUserProfile() {

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}