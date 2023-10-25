package com.letspl.oceankepper.ui.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.FragmentMessageBinding
import com.letspl.oceankepper.databinding.FragmentMyActivityBinding

class MyActivityFragment : Fragment(), BaseActivity.OnBackPressedListener {

    override fun onBackPressed() {
        activity.finish()
    }

    private var _binding: FragmentMyActivityBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy {
        requireActivity() as BaseActivity
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyActivityBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemTab.setTabTextColors(Color.parseColor("#7a7a7a"), Color.parseColor("#545454"))
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}