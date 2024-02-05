package com.letspl.oceankeeper.ui.view

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import com.letspl.oceankeeper.databinding.FragmentRuleBinding
import com.letspl.oceankeeper.ui.viewmodel.SettingViewModel
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModelObserver()
        settingViewModel.getTerms()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupViewModelObserver() {
        settingViewModel.getTermsResult.observe(viewLifecycleOwner) {
            binding.contentTv.text = Html.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        settingViewModel.errorMsg.observe(viewLifecycleOwner) {
            if(it != "") {
                activity.showErrorMsg(it)
            }
        }
    }

    fun onClickedBackBtn() {
        activity.onReplaceFragment(SettingFragment(), false, false)
    }

    override fun onDestroyView() {
        _binding = null
        settingViewModel.clearErrorMsg()
        super.onDestroyView()
    }
}