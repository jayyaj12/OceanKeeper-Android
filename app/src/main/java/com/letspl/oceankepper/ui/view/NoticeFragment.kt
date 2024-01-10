package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.FragmentNoticeBinding
import com.letspl.oceankepper.ui.adapter.NoticeListAdapter
import com.letspl.oceankepper.ui.viewmodel.NoticeViewModel
import com.letspl.oceankepper.util.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NoticeFragment : Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentNoticeBinding? = null
    private val binding: FragmentNoticeBinding get() = _binding!!
    private lateinit var noticeListAdapter: NoticeListAdapter
    private val noticeViewModel: NoticeViewModel by viewModels()
    private val activity by lazy{
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
        _binding = FragmentNoticeBinding.inflate(layoutInflater)
        binding.notiecFragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNoticeListAdapter()
        setupViewModelObserver()
        getNotice()
    }

    // 공지사항 조회
    private fun getNotice() {
        noticeViewModel.getNotice(null, 10)
    }

    private fun setupViewModelObserver() {
        // 공지사항 조회 결과를 listAdapter 에 반영함
        noticeViewModel.getNoticeResult.observe(viewLifecycleOwner) {
            noticeListAdapter.submitList(it.toMutableList())
        }

        // 에러 시 토스트 표시
        noticeViewModel.errorMsg.observe(viewLifecycleOwner) {
            if(it != "") {
                activity.showErrorMsg(it)
            }
        }
    }

    private fun setupNoticeListAdapter() {
        noticeListAdapter = NoticeListAdapter()
        binding.noticeRv.adapter = noticeListAdapter

        // 공지사항 list 의 최하단에 오면 새로운 데이터를 추가로 조회한다.
        binding.noticeRv.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(!binding.noticeRv.canScrollVertically(1)) {
                noticeViewModel.getNotice(noticeViewModel.getLastNoticeId(), 10)
            }
        }
    }

    fun onClickBackBtn() {
        when(EntryPoint.noticePoint) {
            "main" -> activity.onReplaceFragment(MainFragment(), false, true)
            "setting" -> activity.onReplaceFragment(SettingFragment(), false, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        noticeViewModel.setIsLast(false)
        noticeViewModel.clearErrorMsg()
        _binding = null
    }
}