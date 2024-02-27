package com.letspl.oceankeeper.ui.view

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
import com.letspl.oceankeeper.R
import com.letspl.oceankeeper.data.model.ManageApplyMemberModel
import com.letspl.oceankeeper.databinding.FragmentManageApplyMemberBinding
import com.letspl.oceankeeper.ui.adapter.ManageApplyMemberListAdapter
import com.letspl.oceankeeper.ui.dialog.*
import com.letspl.oceankeeper.ui.viewmodel.ManageApplyViewModel
import com.letspl.oceankeeper.ui.viewmodel.MessageViewModel
import com.letspl.oceankeeper.ui.viewmodel.MyActivityViewModel
import com.letspl.oceankeeper.util.AllClickedStatus
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ManageApplyMemberFragment(private val activityId: String) : Fragment(),
    BaseActivity.OnBackPressedListener {
    private var _binding: FragmentManageApplyMemberBinding? = null
    private val binding: FragmentManageApplyMemberBinding get() = _binding!!
    private lateinit var managerApplyListAdapter: ManageApplyMemberListAdapter
    private val manageApplyViewModel: ManageApplyViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()
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
        binding.myActivityViewModel = myActivityViewModel
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
        binding.titleTv.text = myActivityViewModel.getClickItem().title
    }

    // 신청자 리스트 불러오기
    private fun getCrewInfoList() {
        manageApplyViewModel.getCrewInfoList(activityId)
    }

    // 뷰모델 옵저버 세팅
    private fun setupViewModelObserver() {
        // 메세지 전송 결과
        messageViewModel.sendMessageResult.observe(viewLifecycleOwner) {
            if (it) {
                activity.showSuccessMsg("메세지 전송이 정상 처리 되었습니다.")
            }
        }
        // 신청자 리스트 불러온 결과
        manageApplyViewModel.getCrewInfoList.observe(viewLifecycleOwner) {
            managerApplyListAdapter.submitList(it.toMutableList())
        }

        manageApplyViewModel.errorMsg.observe(viewLifecycleOwner) {
            if(it != "") {
                activity.showErrorMsg(it)
            }
        }

        messageViewModel.errorMsg.observe(viewLifecycleOwner) {
            if(it != "") {
                activity.showErrorMsg(it)
            }
        }

        manageApplyViewModel.excelMakeResult.observe(viewLifecycleOwner) {
            if (it) {
                activity.showSuccessMsg("엑셀 다운로드가 성공하였습니다.\n저장된 위치: 다운로드 폴더/오션키퍼/${myActivityViewModel.getClickItem().title}.xlsx")
            }
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

    // 거절하기 버튼 클릭
    fun onClickedRejectBtn() {
        if (!manageApplyViewModel.isClickedEmpty()) {
            val crewList = manageApplyViewModel.getClickedCrewList()
            // 거절하기는 1회 실행 시 한명씩 가능
            if (crewList.size > 1) {
                activity.showErrorMsg("거절하기는 1명씩 가능합니다.")
            } else {
                MakeRejectReasonDialog(requireContext(), crewList[0].nickname) { reason ->
                    // 거절 api 호출
                    manageApplyViewModel.postCrewStatus(manageApplyViewModel.getClickedCrewApplicationIdList(),
                        "REJECT",
                        reason)
                }.show()
            }
        } else {
            activity.showErrorMsg("1명 이상 선택해 주세요.")
        }
    }

    // 노쇼 체크 버튼 클릭
    fun onClickedNoShowBtn() {
        if (!manageApplyViewModel.isClickedEmpty()) {
            NoShowCheckDialog(requireContext()) {
                // 노쇼체크 api 조회
                manageApplyViewModel.postCrewStatus(manageApplyViewModel.getClickedCrewApplicationIdList(),
                    "NO_SHOW")
            }.show()
        } else {
            activity.showErrorMsg("1명 이상 선택해 주세요.")
        }
    }

    // 쪽지 보내기 버튼 클릭
    fun onClickedSendMessage() {
        val clickedNickList = manageApplyViewModel.getClickedCrewNicknameList()
        if (clickedNickList.isNotEmpty()) {
            SendMessageManageDialog(requireContext()) { content ->
                Timber.e("clickedNickList.size ${clickedNickList.size}")
                Timber.e("manageApplyViewModel.getApplyCrewList().size ${manageApplyViewModel.getApplyCrewList().size}")
                if(clickedNickList.size == manageApplyViewModel.getApplyCrewList().size) {
                    messageViewModel.postMessage(activityId,
                        content,
                        clickedNickList,
                        "NOTICE")
                } else {
                    messageViewModel.postMessage(activityId,
                        content,
                        clickedNickList,
                        "PRIVATE")
                }
            }.show()
        } else {
            activity.showErrorMsg("1명 이상 선택해 주세요.")
        }
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

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Timber.e("true")
            if (imagePermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    ), 1
                )

                false
            } else {
                true
            }
        } else {
            Timber.e("else")
            if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
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
        manageApplyViewModel.getCrewInfoFileDownloadUrl(activityId,
            myActivityViewModel.getClickItem().title)
    }

    fun onClickedBackBtn() {
        activity.onReplaceFragment(MyActivityFragment(), false, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        manageApplyViewModel.clearData()
        messageViewModel.clearMessageList()
        manageApplyViewModel.clearErrorMsg()
        messageViewModel.clearErrorMsg()
        manageApplyViewModel.setAllChecked(false)

        _binding = null
    }

}