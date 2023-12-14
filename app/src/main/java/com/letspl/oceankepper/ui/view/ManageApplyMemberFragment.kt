package com.letspl.oceankepper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.model.ManageApplyMemberModel
import com.letspl.oceankepper.databinding.FragmentManageApplyMemberBinding
import com.letspl.oceankepper.databinding.ItemApplyListBinding
import com.letspl.oceankepper.ui.adapter.ManageApplyMemberListAdapter
import com.letspl.oceankepper.ui.viewmodel.ManageApplyViewModel
import com.letspl.oceankepper.util.ApplyMemberStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageApplyMemberFragment(private val activityId: String) : Fragment() {
    private var _binding: FragmentManageApplyMemberBinding? = null
    private val binding: FragmentManageApplyMemberBinding get() = _binding!!
    private lateinit var managerApplyListAdapter: ManageApplyMemberListAdapter
    private val manageApplyViewModel: ManageApplyViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentManageApplyMemberBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupApplyMemberListAdapter()
        setupViewModelObserver()
        getCrewInfoList()
    }

    // 신청자 리스트 불러오기
    private fun getCrewInfoList() {
        manageApplyViewModel.getCrewInfoList(activityId)
    }

    // 뷰모델 옵저버 세팅
    private fun setupViewModelObserver() {
        // 신청자 리스트 불러온 결과
        manageApplyViewModel.getCrewInfoList.observe(viewLifecycleOwner) {
            managerApplyListAdapter.submitList(it.toMutableList())
        }

        manageApplyViewModel.errorMsg.observe(viewLifecycleOwner) {
            activity.showErrorMsg(it)
        }

    }

    // 신청자 리스트 셋업
    private fun setupApplyMemberListAdapter() {
        managerApplyListAdapter = ManageApplyMemberListAdapter()
        binding.applyListRv.adapter = managerApplyListAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}