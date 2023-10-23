package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.NoticeItemDto
import com.letspl.oceankepper.databinding.FragmentNoticeBinding
import com.letspl.oceankepper.ui.adapter.NoticeListAdapter

class NoticeFragment : Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentNoticeBinding? = null
    private val binding: FragmentNoticeBinding get() = _binding!!
    private lateinit var noticeListAdapter: NoticeListAdapter
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
    }

    private fun setupNoticeListAdapter() {
        noticeListAdapter = NoticeListAdapter()
        binding.noticeRv.adapter = noticeListAdapter

        val arr = arrayListOf<NoticeItemDto>(
            NoticeItemDto(0, "제목1", "컨텐츠1", "2023.03.21"),
            NoticeItemDto(1, "제목2", "컨텐츠2", "2023.03.21"),
            NoticeItemDto(2, "제목3", "컨텐츠3", "2023.03.21"),
            NoticeItemDto(3, "제목4", "컨텐츠4", "2023.03.21"),
            NoticeItemDto(4, "제목5", "컨텐츠5", "2023.03.21"),
        )

        noticeListAdapter.submitList(arr)
    }

    fun onClickBackBtn() {
        activity.onReplaceFragment(MainFragment(), false, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}