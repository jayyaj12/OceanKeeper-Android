package com.letspl.oceankeeper.ui.view

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.tabs.TabLayout
import com.letspl.oceankeeper.databinding.FragmentMyActivityBinding
import com.letspl.oceankeeper.ui.adapter.ApplyActivityListAdapter
import com.letspl.oceankeeper.ui.adapter.OpenActivityListAdapter
import com.letspl.oceankeeper.ui.dialog.*
import com.letspl.oceankeeper.ui.viewmodel.MainViewModel
import com.letspl.oceankeeper.ui.viewmodel.MyActivityViewModel
import com.letspl.oceankeeper.util.EntryPoint
import com.letspl.oceankeeper.util.ImgFileMaker
import com.letspl.oceankeeper.util.RotateTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date


@AndroidEntryPoint
class MyActivityFragment : Fragment(), BaseActivity.OnBackPressedListener {
    override fun onBackPressed() {
        activity.finish()
    }

    private var _binding: FragmentMyActivityBinding? = null
    private val binding get() = _binding!!
    private val activity by lazy {
        requireActivity() as BaseActivity
    }
    private val mainViewModel: MainViewModel by viewModels()
    private val myActivityViewModel: MyActivityViewModel by viewModels()
    private lateinit var choiceProfileImageDialog: ChoiceProfileImageDialog
    private lateinit var applyActivityListAdapter: ApplyActivityListAdapter
    private lateinit var openActivityListAdapter: OpenActivityListAdapter
    private lateinit var applyCancelDialog: ApplyCancelDialog
    private lateinit var recruitCancelDialog: RecruitCancelDialog
    private lateinit var rejectReasonDialog: RejectReasonDialog
    private lateinit var editNicknameDialog: EditNicknameDialog

    // 사진 찍기 결과
    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val path = ImgFileMaker.getFullPathFromUri(
                        requireContext(),
                        myActivityViewModel.getTakePhotoUri()
                    )!!
                    val angle = RotateTransform.getRotationAngle(path)
                    val rotateBitmap =
                        RotateTransform.rotateImage(
                            BitmapFactory.decodeFile(path),
                            angle.toFloat(),
                            myActivityViewModel.getTakePhotoUri()
                        )

                    myActivityViewModel.uploadEditProfileImage(
                        ImgFileMaker.saveBitmapToFile(
                            rotateBitmap!!,
                            path
                        )
                    )

                    Glide.with(requireContext())
                        .load(myActivityViewModel.getTakePhotoUri())
                        .fitCenter()
                        .centerCrop()
                        .into(binding.userProfileIv)
                } catch (e: Exception) {
                    activity.showErrorMsg("해당 이미지는 사용할 수 없습니다.")
                }
            }
        }
    }

    // 사진 가져오기 결과
    @RequiresApi(Build.VERSION_CODES.O)
    private val choicePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == -1) {
            lifecycleScope.launch {
                val imageUri = result.data?.data
                withContext(Dispatchers.Main) {
                    try {
                        val path = ImgFileMaker.getFullPathFromUri(requireContext(), imageUri)!!
                        val angle = RotateTransform.getRotationAngle(path)
                        val rotateBitmap = RotateTransform.rotateImage(
                            BitmapFactory.decodeFile(path),
                            angle.toFloat(),
                            imageUri
                        )

                        myActivityViewModel.uploadEditProfileImage(
                            ImgFileMaker.saveBitmapToFile(
                                rotateBitmap!!,
                                path
                            )
                        )

                        Glide.with(requireContext())
                            .load(imageUri)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(binding.userProfileIv)

                    } catch (e: Exception) {
                        activity.showErrorMsg("해당 이미지는 사용할 수 없습니다.")
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMyActivityBinding.inflate(layoutInflater)
        binding.myActivityViewModel = myActivityViewModel
        binding.myActivityFragment = this
        binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabLayout()
        getActivityInfo()
        setupUserProfile()
        setupChoiceProfileImageDialog()
        setupApplyActivityListAdapter()
        setupOpenActivityListAdapter()
        setupViewModelObserver()
    }

    // 나의 오션키퍼 활동 정보 불러오기
    private fun getActivityInfo() {
        myActivityViewModel.getMyActivityInfo()
    }

    // 내 활동 불러오기
    private fun getUserActivity(role: String) {
        myActivityViewModel.getUserActivity(role, null)
    }

    private fun setupViewModelObserver() {
        myActivityViewModel.errorMsg.observe(viewLifecycleOwner) {
            if (it != "") {
                activity.showErrorMsg(it)
            }
        }

        // 프로필 이미지 수정
        myActivityViewModel.changeProfileImageResult.observe(viewLifecycleOwner) {
            if (it) {
                activity.showSuccessMsg("프로필 이미지가 수정되었습니다.")
            }
        }
        // 내활동보기 결과 등록(크루)
        myActivityViewModel.getUserActivityCrew.observe(viewLifecycleOwner) {
            if (it != null) {
                Timber.e("getUserActivityCrew ${it.size}")
                binding.openActivityRv.visibility = View.GONE
                binding.applyActivityRv.visibility = View.VISIBLE
                applyActivityListAdapter.submitList(it.toMutableList())

                if (it.isNotEmpty()) {
                    binding.notApplyActivityTv.visibility = View.GONE
                    binding.notApplyActivityIv.visibility = View.GONE
                } else {
                    binding.notApplyActivityTv.text = "신청한 활동이 없습니다."
                    binding.notApplyActivityTv.visibility = View.VISIBLE
                    binding.notApplyActivityIv.visibility = View.VISIBLE
                }
            }
        }
        // 내활동보기 결과 등록(호스트)
        myActivityViewModel.getUserActivityHost.observe(viewLifecycleOwner) {
            if (it != null) {
                Timber.e("getUserActivityHost ${it.size}")
                binding.applyActivityRv.visibility = View.GONE
                binding.openActivityRv.visibility = View.VISIBLE
                openActivityListAdapter.submitList(it.toMutableList())

                if (it.isNotEmpty()) {
                    binding.notApplyActivityTv.visibility = View.GONE
                    binding.notApplyActivityIv.visibility = View.GONE
                } else {
                    binding.notApplyActivityTv.text = "오픈한 활동이 없습니다."
                    binding.notApplyActivityTv.visibility = View.VISIBLE
                    binding.notApplyActivityIv.visibility = View.VISIBLE
                }
            }
        }
        // 활동 지원 취소
        myActivityViewModel.deleteApplyCancel.observe(viewLifecycleOwner) {
            if (it) {
                myActivityViewModel.getUserActivity("crew", null)
            }
        }
        // 모집 취소
        myActivityViewModel.deleteRecruitCancel.observe(viewLifecycleOwner) {
            if (it) {
                myActivityViewModel.getUserActivity("host", null)
            }
        }

        // 닉네임 수정
        myActivityViewModel.changeNicknameResult.observe(viewLifecycleOwner) {
            binding.nicknameTv.text = it
        }
    }

    // 닉네임 변경 버튼 클릭
    fun onClickChangeNickname() {
        editNicknameDialog = EditNicknameDialog(requireContext()) {
            myActivityViewModel.getDuplicateNickname(it)
        }
        editNicknameDialog.show()
    }

    // 모집하기 버튼 클릭
    fun onClickRecruit() {
        activity.onReplaceFragment(ActivityRecruitFragment(), false, false)
    }

    private fun setupApplyActivityListAdapter() {
        applyActivityListAdapter = ApplyActivityListAdapter(requireContext(), { activityId ->
            EntryPoint.activityDetail = "myActivity"
            mainViewModel.setClickedActivityId(activityId)
            activity.onReplaceFragment(ActivityDetailFragment(), false, false)
        }, {
            // 활동 신청서 수정 페이지로 이동
            activity.onReplaceFragment(EditActivityApplyFragment(it))
        }, {
            // 신청 취소 모달 표시
            applyCancelDialog = ApplyCancelDialog(requireContext()) {
                // 활동 취소 api 호출
                myActivityViewModel.deleteApplyCancel(it)
            }
            applyCancelDialog.show()
        }, { reason ->
            // 거절 사유 모달 표시
            rejectReasonDialog = RejectReasonDialog(requireContext(), reason)
            rejectReasonDialog.show()
        })
        binding.applyActivityRv.adapter = applyActivityListAdapter

        binding.applyActivityRv.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            if (!binding.applyActivityRv.canScrollVertically(1)) {
                myActivityViewModel.getUserActivity(
                    "crew",
                    myActivityViewModel.getLastCrewActivityId()
                )
            }
        }
    }

    private fun setupOpenActivityListAdapter() {
        openActivityListAdapter = OpenActivityListAdapter(requireContext(), { activityId ->
            EntryPoint.activityDetail = "myActivity"
            mainViewModel.setClickedActivityId(activityId)
            activity.onReplaceFragment(ActivityDetailFragment(), false, false)
        }, { activityId, item ->
            myActivityViewModel.setClickItem(item)
            // 신청서 관리 페이지로 이동
            activity.onReplaceFragment(ManageApplyMemberFragment(activityId), false)
        }, { activityId ->
            // 모집 수정하기 페이지로 이동
            activity.onReplaceFragment(EditActivityRecruitFragment(activityId), false, false)
        }, { activityId ->
            // 모집 취소 모달 표시
            recruitCancelDialog = RecruitCancelDialog(requireContext()) {
                // 활동 모집 취소 api 호출
                myActivityViewModel.deleteRecruitmentCancel(activityId)
            }
            recruitCancelDialog.show()
        })
        binding.openActivityRv.adapter = openActivityListAdapter

        binding.openActivityRv.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            if (!binding.openActivityRv.canScrollVertically(1)) {
                myActivityViewModel.getUserActivity(
                    "host",
                    myActivityViewModel.getLastHostActivityId()
                )
            }
        }
    }

    // 유저 프로필 표시
    private fun setupUserProfile() {
        Glide.with(requireContext())
            .load(myActivityViewModel.getUserProfile())
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .dontTransform()
            .into(binding.userProfileIv)
    }

    // 촬영, 앨범에서 선택 팝업 세팅
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupChoiceProfileImageDialog() {
        choiceProfileImageDialog = ChoiceProfileImageDialog(requireContext(), {
            // 사진 촬영
            myActivityViewModel.setTakePhotoUri(createImageFile())
            takePhoto.launch(myActivityViewModel.getTakePhotoUri())
        }, {
            // 앨범에서 사진 선택
            val intent = Intent(Intent.ACTION_PICK)
            // intent와 data와 type을 동시에 설정하는 메서드
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
            )
            choicePhoto.launch(intent)
        })
    }

    // temp 이미지 파일 생성
    private fun createImageFile(): Uri? {
        val now = SimpleDateFormat("yyMMdd_HHmmss").format(Date())
        val content = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "img_$now.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        }
        return requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            content
        )
    }

    // 프로필 수정 아이콘 클릭 시 촬영, 앨범에서 선택 팝업 표시
    fun onClickedChoiceProfileImage() {
        choiceProfileImageDialog.show()
    }

    // 설정 페이지 이동
    fun onMoveSetting() {
        EntryPoint.settingPoint = "myActivity"
        activity.onReplaceFragment(SettingFragment(), false, false)
    }

    // 알림 페이지 이동
    fun onMoveNotification() {
        EntryPoint.notificationPoint = "myActivity"
        activity.onReplaceFragment(NotificationFragment(), false, false)
    }

    // tablayout 세팅
    private fun setupTabLayout() {
        binding.itemTab.setTabTextColors(Color.parseColor("#7a7a7a"), Color.parseColor("#545454"))
        binding.itemTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Timber.e("tab?.position ${tab?.position}")
                when (tab?.position) {
                    0 -> {
                        binding.notApplyActivityTv.visibility = View.GONE
                        binding.notApplyActivityIv.visibility = View.GONE

                        binding.applyActivityRv.visibility = View.GONE
                        binding.openActivityRv.visibility = View.GONE
                        myActivityViewModel.setCrewLast(false)
                        myActivityViewModel.setHostLast(false)
                    }

                    1 -> {
                        getUserActivity("crew")
                        myActivityViewModel.setHostLast(false)
                    }

                    2 -> {
                        getUserActivity("host")
                        myActivityViewModel.setCrewLast(false)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myActivityViewModel.clearLivedata()
        mainViewModel.clearErrorMsg()
        myActivityViewModel.clearErrorMsg()

        _binding = null
    }
}