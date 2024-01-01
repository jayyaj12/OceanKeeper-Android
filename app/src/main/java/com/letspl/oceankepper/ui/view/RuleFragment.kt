package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.FragmentRuleBinding
import com.letspl.oceankepper.databinding.FragmentSettingBinding
import com.letspl.oceankepper.ui.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RuleFragment : Fragment(), BaseActivity.OnBackPressedListener {

    private var _binding: FragmentRuleBinding? = null
    private val binding: FragmentRuleBinding get() = _binding!!
    private val activity:BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    private val settingViewModel: SettingViewModel by viewModels()

    override fun onBackPressed() {
        onClickedBackBtn()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRuleBinding.inflate(layoutInflater)
        binding.ruleFragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingViewModel.getPrivacyPolicy()
    }

    fun onClickedBackBtn() {
        activity.onReplaceFragment(SettingFragment(), false, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}