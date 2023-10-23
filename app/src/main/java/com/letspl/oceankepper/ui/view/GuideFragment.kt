package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.dto.GuideItemDto
import com.letspl.oceankepper.databinding.FragmentGuideBinding
import com.letspl.oceankepper.ui.adapter.GuideListAdapter


class GuideFragment : Fragment(), BaseActivity.OnBackPressedListener {

    private var _binding: FragmentGuideBinding? = null
    val binding:FragmentGuideBinding get() = _binding!!

    private lateinit var guideListAdapter: GuideListAdapter
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onBackPressed() {
        onClickBackBtn()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGuideBinding.inflate(layoutInflater)
        binding.guideFragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGuideListAdapter()
    }

    private fun setupGuideListAdapter() {
        guideListAdapter = GuideListAdapter() {
            activity.onReplaceFragment(GuideDetailFragment(it))
        }
        binding.guideRv.adapter = guideListAdapter

        val arr = arrayListOf<GuideItemDto>(
            GuideItemDto(1, "바다살리기 네트워크와 해양정화활동 시작하기", "컨텐트1", "2023-10-23T07:33:07.135Z", "B9EsAERudp8"),
            GuideItemDto(2, "잠깐! 해양쓰레기 그냥 주우면 안된다고?", "컨텐트2", "2023-10-23T07:33:07.135Z","B9EsAERudp8"),
            GuideItemDto(3, "바다살리기 네트워크와 해양정화활동 시작하기", "컨텐트3", "2023-10-23T07:33:07.135Z","B9EsAERudp8")
        )
        guideListAdapter.submitList(arr)
    }

    fun onClickBackBtn() {
        activity.onReplaceFragment(MainFragment(), false, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}