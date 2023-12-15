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
import com.letspl.oceankepper.ui.adapter.ManageApplyMemberListAdapter
import com.letspl.oceankepper.ui.viewmodel.ManageApplyViewModel
import com.letspl.oceankepper.util.AllClickedStatus
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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
        binding.manageApplyViewModel = manageApplyViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupApplyMemberListAdapter()
        setupViewModelObserver()
//        getCrewInfoList()
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

        manageApplyViewModel.allClicked.observe(viewLifecycleOwner) {
            if(manageApplyViewModel.getTempApplyCrewList().isNotEmpty()) {
                managerApplyListAdapter.submitList(manageApplyViewModel.getTempApplyCrewList()
                    .toMutableList())
            }
        }
    }

    // 신청자 리스트 셋업
    private fun setupApplyMemberListAdapter() {
        val arr = arrayListOf<ManageApplyMemberModel.CrewInfoDto>()
        arr.add(ManageApplyMemberModel.CrewInfoDto(
            "ㅂㅈㄷㅂㅈㄷ",
            "REJECT",
            "김제주",
            1,
            "제주돌고래",
            false)
        )
        arr.add(ManageApplyMemberModel.CrewInfoDto(
            "ㅂㅈㄷㅂㅈㄷ",
            "CONFIRM",
            "기모치",
            2,
            "기모치이",
            false)
        )
        arr.add(ManageApplyMemberModel.CrewInfoDto(
            "ㅂㅈㄷㅂㅈㄷ",
            "NOSHOW",
            "기모치",
            3,
            "기모치이",
            false)
        )

        ManageApplyMemberModel.applyCrewList = arr

        managerApplyListAdapter = ManageApplyMemberListAdapter({ allClicked ->
            // 리스트 체크 선택 시 전체 선택하기 체크박스 체크 여부
            when (allClicked) {
                AllClickedStatus.AllClicked -> {
                    binding.allChoiceIv.setBackgroundResource(R.drawable.checkbox_checked)
                    manageApplyViewModel.setAllChecked(true)
                }
                AllClickedStatus.NotAllClicked -> {
                    binding.allChoiceIv.setBackgroundResource(R.drawable.checkbox_default)
                    manageApplyViewModel.setAllChecked(false)
                }
                else -> {
                    binding.allChoiceIv.setBackgroundResource(R.drawable.checkbox_default)
                    manageApplyViewModel.setAllChecked(false)
                }
            }

        }, { applicationId ->
            // 크루원 상세 페이지로 이동
            activity.onReplaceFragment(CrewDetailFragment(applicationId))
        })
        binding.applyListRv.adapter = managerApplyListAdapter
        managerApplyListAdapter.submitList(arr.toMutableList())
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}