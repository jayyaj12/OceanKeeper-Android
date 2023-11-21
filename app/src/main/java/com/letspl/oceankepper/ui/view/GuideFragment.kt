package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.FragmentGuideBinding
import com.letspl.oceankepper.ui.adapter.GuideListAdapter
import com.letspl.oceankepper.ui.viewmodel.GuideViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuideFragment : Fragment(), BaseActivity.OnBackPressedListener {

    private var _binding: FragmentGuideBinding? = null
    val binding:FragmentGuideBinding get() = _binding!!

    private lateinit var guideListAdapter: GuideListAdapter
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    private val guideViewModel: GuideViewModel by viewModels()

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
        setupViewModelObserver()
        getGuide()
    }

    private fun setupViewModelObserver() {
        // 공지사항 조회 결과를 listAdapter 에 반영함
        guideViewModel.getGuideResult.observe(viewLifecycleOwner) {
            guideListAdapter.submitList(it.toMutableList())
        }

        // 에러 시 토스트 표시
        guideViewModel.errorMsg.observe(viewLifecycleOwner) {
            activity.showErrorMsg(it)
        }
    }

    private fun getGuide() {
        guideViewModel.getGuide(null, 10)
    }

    private fun setupGuideListAdapter() {
        guideListAdapter = GuideListAdapter { videoId, videoName ->
            activity.onReplaceFragment(GuideDetailFragment(videoId, videoName))
        }
        binding.guideRv.adapter = guideListAdapter

        // 이용가이드 list 의 최하단에 오면 새로운 데이터를 추가로 조회한다.
        binding.guideRv.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(!binding.guideRv.canScrollVertically(1)) {
                guideViewModel.getGuide(guideViewModel.getLastGuideId(), 10)
            }
        }
    }

    fun onClickBackBtn() {
        activity.onReplaceFragment(MainFragment(), false, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        guideViewModel.setIsLast(false)
        _binding = null
    }

}