package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding:FragmentSettingBinding get() = _binding!!
    private val activity:BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun onClickedBackBtn() {
        activity.onReplaceFragment(MainFragment(), false, true, 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}