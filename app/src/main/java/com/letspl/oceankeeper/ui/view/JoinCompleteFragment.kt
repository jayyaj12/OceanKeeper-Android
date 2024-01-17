package com.letspl.oceankeeper.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.letspl.oceankeeper.databinding.FragmentJoinCompleteBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class JoinCompleteFragment: Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentJoinCompleteBinding? = null
    private val binding: FragmentJoinCompleteBinding get() = _binding!!
    val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentJoinCompleteBinding.inflate(layoutInflater)
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

        // 확인 버튼 클릭
        binding.okBtn.setOnClickListener {
            activity.onReplaceFragment(LoginFragment(), false, true)
        }
    }
    override fun onBackPressed() {
        activity.finish()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}