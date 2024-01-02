package com.letspl.oceankepper.ui.view

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.letspl.oceankepper.R
import com.letspl.oceankepper.data.model.ManageApplyMemberModel
import com.letspl.oceankepper.databinding.FragmentManageApplyMemberBinding
import com.letspl.oceankepper.ui.adapter.ManageApplyMemberListAdapter
import com.letspl.oceankepper.ui.dialog.DeleteListDialog
import com.letspl.oceankepper.ui.dialog.MakeRejectReasonDialog
import com.letspl.oceankepper.ui.dialog.NoShowCheckDialog
import com.letspl.oceankepper.ui.dialog.RejectReasonDialog
import com.letspl.oceankepper.ui.viewmodel.ManageApplyViewModel
import com.letspl.oceankepper.ui.viewmodel.MyActivityViewModel
import com.letspl.oceankepper.util.AllClickedStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@AndroidEntryPoint
class ManageApplyMemberFragment(private val activityId: String) : Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentManageApplyMemberBinding? = null
    private val binding: FragmentManageApplyMemberBinding get() = _binding!!
    private lateinit var managerApplyListAdapter: ManageApplyMemberListAdapter
    private val manageApplyViewModel: ManageApplyViewModel by viewModels()
    private val myActivityViewModel: MyActivityViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onBackPressed() {
        onClickedBackBtn()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentManageApplyMemberBinding.inflate(layoutInflater)
        binding.manageApplyViewModel = manageApplyViewModel
        binding.manageApplyFragment = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProjectName()
        checkGalleryPermission()
        setupApplyMemberListAdapter()
        setupViewModelObserver()
        getCrewInfoList()
    }

    private fun setupProjectName() {
        binding.titleTv.text = myActivityViewModel.getClickProjectName()
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
            // 변경한 값으로 list update
            if (manageApplyViewModel.getTempApplyCrewList().isNotEmpty()) {
                managerApplyListAdapter.submitList(
                    ManageApplyMemberModel.tempApplyCrewList.toMutableList()
                )

                // temp data 를 applyCrewList 에 update
                ManageApplyMemberModel.applyCrewList =
                    ManageApplyMemberModel.tempApplyCrewList.toMutableList() as ArrayList<ManageApplyMemberModel.CrewInfoDto>
                ManageApplyMemberModel.tempApplyCrewList.clear()
            }
        }
    }

    // 신청자 리스트 셋업
    private fun setupApplyMemberListAdapter() {
//        val arr = arrayListOf<ManageApplyMemberModel.CrewInfoDto>()
//        arr.add(
//            ManageApplyMemberModel.CrewInfoDto(
//                "ㅂㅈㄷㅂㅈㄷ",
//                "REJECT",
//                "김제주",
//                1,
//                "제주돌고래",
//                false
//            )
//        )
//        arr.add(
//            ManageApplyMemberModel.CrewInfoDto(
//                "ㅂㅈㄷㅂㅈㄷ",
//                "IN_PROGRESS",
//                "기모치",
//                2,
//        "기모치이",
//        false
//        )
//        )
//        arr.add(
//            ManageApplyMemberModel.CrewInfoDto(
//                "ㅂㅈㄷㅂㅈㄷ",
//                "NO_SHOW",
//                "기모치",
//                3,
//                "기모치이",
//                false
//            )
//        )
//
//        ManageApplyMemberModel.applyCrewList = arr

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
            activity.onReplaceFragment(CrewDetailFragment(applicationId, activityId))
        })
        binding.applyListRv.adapter = managerApplyListAdapter

//        managerApplyListAdapter.submitList(ManageApplyMemberModel.applyCrewList)
    }

    // 삭제하기 버튼 클릭
    fun onClickedDeleteBtn() {
        DeleteListDialog(requireContext()) {
            val crewList = manageApplyViewModel.getClickedCrewList()

            if (!manageApplyViewModel.isClickedEmpty()) {
                // 삭제하기 api 조회
            } else {
                activity.showErrorMsg("1명 이상 선택 해주세요.")
            }
        }.show()
    }

    // 거절하기 버튼 클릭
    fun onClickedRejectBtn() {
        val crewList = manageApplyViewModel.getClickedCrewList()

        if (!manageApplyViewModel.isClickedEmpty()) {
            // 거절하기는 1회 실행 시 한명씩 가능
            if (crewList.size > 1) {
                activity.showErrorMsg("거절하기는 1명씩 가능합니다.")
            } else {
                MakeRejectReasonDialog(requireContext(), crewList[0].nickname) {reason ->
                    // 거절 api 호출
                    manageApplyViewModel.postCrewStatus(manageApplyViewModel.getClickedCrewApplicationIdList(), "REJECT", reason)
                }.show()
            }
        } else {
            activity.showErrorMsg("1명 이상 선택 해주세요.")
        }
    }

    // 노쇼 체크 버튼 클릭
    fun onClickedNoShowBtn() {
        NoShowCheckDialog(requireContext()) {
            val crewList = manageApplyViewModel.getClickedCrewList()

            if (!manageApplyViewModel.isClickedEmpty()) {
                // 노쇼체크 api 조회
                manageApplyViewModel.postCrewStatus(manageApplyViewModel.getClickedCrewApplicationIdList(), "NO_SHOW")
            } else {
                activity.showErrorMsg("1명 이상 선택 해주세요.")
            }
        }.show()
    }

    private fun checkGalleryPermission(): Boolean {
        val writePermission = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val imagePermission = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES
        )

        return if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
            Timber.e("true")
            if(imagePermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    ), 1
                )

                false
            } else {
                true
            }
        } else{
            Timber.e("else")
            if(writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ), 2
                )
                false
            } else {
                true
            }
        }
    }

    // 노쇼 체크 버튼 클릭
    fun onClickedDownloadExcel() {
        manageApplyViewModel.getCrewInfoFileDownloadUrl(activityId, myActivityViewModel.getClickProjectName())
    }

    fun onClickedBackBtn() {
        activity.onReplaceFragment(MyActivityFragment(), false, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        manageApplyViewModel.clearData()

        _binding = null
    }

}