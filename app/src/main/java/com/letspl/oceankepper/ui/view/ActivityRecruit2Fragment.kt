package com.letspl.oceankepper.ui.view

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.FragmentActivityRecruit2Binding
import com.letspl.oceankepper.ui.dialog.RecruitActivityCompleteDialog
import com.letspl.oceankepper.ui.viewmodel.ActivityRecruit2ViewModel
import com.letspl.oceankepper.ui.viewmodel.ActivityRecruitViewModel
import com.letspl.oceankepper.util.ImgFileMaker
import com.letspl.oceankepper.util.ResizingImage
import com.letspl.oceankepper.util.RotateTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class ActivityRecruit2Fragment : Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentActivityRecruit2Binding? = null
    private val binding: FragmentActivityRecruit2Binding get() = _binding!!
    private val activityRecruitViewModel: ActivityRecruitViewModel by viewModels()
    private lateinit var mActivityResultLauncher: ActivityResultLauncher<Intent>
    @Inject
    lateinit var activityRecruit2ViewModel: ActivityRecruit2ViewModel
    private val REQ_GALLERY = 1000
    private val RESULT_OK = -1
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    private val resizingImage = ResizingImage()

    // 썸네일 갤러리 선택 시 결과
    private val thumbnailImageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                lifecycleScope.launch {
                    try {
                        val path = ImgFileMaker.getFullPathFromUri(requireContext(), it)!!
                        val angle = RotateTransform.getRotationAngle(path)
                        val rotateBitmap = RotateTransform.rotateImage(
                            BitmapFactory.decodeFile(path),
                            angle.toFloat(),
                            it
                        )

                        activityRecruit2ViewModel.setThumbnailImageFile(
                            ImgFileMaker.saveBitmapToFile(rotateBitmap!!, path)
                        )

                        Glide.with(requireActivity()).load(imageUri).fitCenter()
                            .into(binding.thumbnailIv)

                        binding.thumbnailPhotoCl.setBackgroundResource(R.drawable.custom_radius_8_stroke_g300_solid_fff)
                        binding.thumbnailPhotoTv.visibility = View.GONE
                    } catch (e: Exception) {
                        activity.showErrorMsg("해당 이미지는 사용할 수 없습니다.")
                    }
                }
            }
        }
    }

    // 키퍼 소개 갤러리 선택 시 결과
    private val keeperIntroduceImageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                lifecycleScope.launch {
                    try {
                        val path = ImgFileMaker.getFullPathFromUri(requireContext(), it)!!
                        val angle = RotateTransform.getRotationAngle(path)
                        val rotateBitmap = RotateTransform.rotateImage(
                            BitmapFactory.decodeFile(path),
                            angle.toFloat(),
                            it
                        )

                        activityRecruit2ViewModel.setKeeperIntroduceImageFile(
                            ImgFileMaker.saveBitmapToFile(rotateBitmap!!, path)
                        )

                        Glide.with(requireActivity()).load(imageUri).fitCenter()
                            .into(binding.introduceKeeperIv)

                        binding.introduceKeeperPhotoCl.setBackgroundResource(R.drawable.custom_radius_8_stroke_g300_solid_fff)
                        binding.introduceKeeperPhotoTv.visibility = View.GONE
                    } catch (e: Exception) {
                        activity.showErrorMsg("해당 이미지는 사용할 수 없습니다.")
                    }
                }
            }
        }
    }

    // 활동 스토리 갤러리 선택 시 결과
    private val activityStoryImageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                lifecycleScope.launch {
                    try {
                        val path = ImgFileMaker.getFullPathFromUri(requireContext(), it)!!
                        val angle = RotateTransform.getRotationAngle(path)
                        val rotateBitmap = RotateTransform.rotateImage(
                            BitmapFactory.decodeFile(path),
                            angle.toFloat(),
                            it
                        )

                        activityRecruit2ViewModel.setActivityStoryImageFile(
                            ImgFileMaker.saveBitmapToFile(rotateBitmap!!, path)
                        )

                        Glide.with(requireActivity()).load(imageUri).fitCenter()
                            .into(binding.activityStoryIv)

                        binding.activityStoryPhotoCl.setBackgroundResource(R.drawable.custom_radius_8_stroke_g300_solid_fff)
                        binding.activityStoryPhotoTv.visibility = View.GONE
                    } catch (e: Exception) {
                        activity.showErrorMsg("해당 이미지는 사용할 수 없습니다.")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            //기존의 startActivityForResult(intent)에 해당
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentActivityRecruit2Binding.inflate(layoutInflater)
        binding.activityRecruit2Fragment = this
        binding.activityRecruit2ViewModel = activityRecruit2ViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEditTextListener()
        setupViewModelObserver()

        Timber.e("1 ${activityRecruitViewModel.getRecruitStartDate()}")
        Timber.e("2 ${activityRecruitViewModel.getRecruitEndDate()}")
        Timber.e("3 ${activityRecruitViewModel.getActivityStartDate()}")
    }

    private fun setupViewModelObserver() {
        activityRecruit2ViewModel.errorMsg.observe(viewLifecycleOwner) {
            activity.showErrorMsg(it)
        }

        // 활동 모집 등록 성공 여부
        activityRecruit2ViewModel.recruitActivityIsSuccess.observe(viewLifecycleOwner) {
            if(it) {
                activityRecruitViewModel.clearTempData()
                activityRecruit2ViewModel.clearData()

                val dialog = RecruitActivityCompleteDialog(requireContext(),
                    "활동 모집 등록 완료",
                    activityRecruit2ViewModel.getRecruitCompleteText(),
                    {
                        // 나의 활동 확인하기

                    },
                    {
                        // 확인 버튼
                        activity.onReplaceFragment(MainFragment(), false, true, 1)
                    })

                dialog.setCancelable(false)
                dialog.show()
            }
        }
    }

    // edittext 리스트 셋업
    private fun setupEditTextListener() {
        binding.introduceKeeperEt.addTextChangedListener {
            activityRecruit2ViewModel.onChangedKeeperIntroduceEditText(it.toString())
        }
        binding.activityStoryEt.addTextChangedListener {
            activityRecruit2ViewModel.onChangedActivityStoryEditText(it.toString())
        }
    }

    private fun checkGalleryPermission(): Boolean {
        val writePermission = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val READ_MEDIA_IMAGES = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES
        )


        // 권한 확인
        return if (READ_MEDIA_IMAGES == PackageManager.PERMISSION_DENIED ||writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {

            Timber.e("writePermission == PackageManager.PERMISSION_DENIED ${writePermission == PackageManager.PERMISSION_DENIED}")
            Timber.e("readPermission == PackageManager.PERMISSION_DENIED ${readPermission == PackageManager.PERMISSION_DENIED}")
            // 권한 요청
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ), REQ_GALLERY
            )
//            val intent =
//                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//            intent.data = Uri.parse("package:${requireActivity().packageName}")
//
//
//            mActivityResultLauncher.launch(intent)
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Timber.e("requestCode $requestCode")

        when(requestCode) {
            REQ_GALLERY -> {// 권한이 비어있는 경우 에러
                if (grantResults.isEmpty()) {
                    throw RuntimeException("Empty Permission Result")
                }
                // 거부된 권한이 있는지 확인한다
                var isPermitted = true
                val deniedPermission = ArrayList<String>()
                for ((id, result) in grantResults.withIndex()) {
                    if (result == PackageManager.PERMISSION_DENIED) {
                        isPermitted = false
                        deniedPermission.add(permissions[id])
                    }
                }
                // 권한이 모두 충족된 경우 다이얼로그를 보여준다
                if (isPermitted) {
//                    showRecordDialog()
                } else {
                    // 거부만 선택한 경우
                    if (
                        ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    ) {
                        // 권한이 필요하다는 토스트 메시지를 띄운다

                        // 권한을 다시 요청한다
                        requestPermissions(deniedPermission.toArray(arrayOfNulls<String>
                            (deniedPermission.size)), 0)
                    }
// 거부 및 다시보지 않기를 선택한 경우
                    else {
                        Timber.e("// 거부 및 다시보지 않기를 선택한 경우")
                        // 권한 설정으로 이동할 수 있도록 알림창을 띄운다
//                        super.showDialogToGetPermission(this)
                    }
                }

            }
        }


    }

    fun selectThumbnailGallery() {
        Timber.e("checkGalleryPermission ${checkGalleryPermission()}")
        if (checkGalleryPermission()) {
            Timber.e("checkGalleryPermission2 ${checkGalleryPermission()}")

            // 권한이 있는 경우 갤러리 실행
            val intent = Intent(Intent.ACTION_PICK)
            // intent와 data와 type을 동시에 설정하는 메서드
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
            )

            thumbnailImageResult.launch(intent)
        }
    }

    fun selectKeeperIntroduceGallery() {
        if (checkGalleryPermission()) {
            // 권한이 있는 경우 갤러리 실행
            val intent = Intent(Intent.ACTION_PICK)
            // intent와 data와 type을 동시에 설정하는 메서드
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
            )

            keeperIntroduceImageResult.launch(intent)
        }
    }

    fun selectActivityStoryGallery() {
        if (checkGalleryPermission()) {
            // 권한이 있는 경우 갤러리 실행
            val intent = Intent(Intent.ACTION_PICK)
            // intent와 data와 type을 동시에 설정하는 메서드
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"
            )

            activityStoryImageResult.launch(intent)
        }
    }

    // 완료 버튼 클릭
    fun onClickedCompleteBtn() {
        if (activityRecruit2ViewModel.isExistNeedData()) {
            activityRecruit2ViewModel.activityRegister(
                activityStory = binding.activityStoryEt.text.toString(),
                etc = activityRecruitViewModel.getOtherGuide(),
                garbageCategory = activityRecruitViewModel.getRecruitCategoryStringValue(),
                keeperIntroduction = binding.introduceKeeperEt.text.toString(),
                locationTag = activityRecruitViewModel.getRecruitLocationStringValue(),
                preparation = activityRecruitViewModel.getMaterial(),
                programDetails = activityRecruitViewModel.getGuideActivity(),
                quota = activityRecruitViewModel.getQuota(),
                rewards = activityRecruitViewModel.getGiveRewardStr(),
                title = activityRecruitViewModel.getProjectName(),
                transportation = activityRecruitViewModel.getGuideTrafficStringValue()
            )
        } else {
            Toast.makeText(requireContext(), "모든 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 뒤로가기 버튼 클릭
    fun onClickedBackBtn() {
        activityRecruit2ViewModel.clearData()
        activity.onReplaceFragment(ActivityRecruitFragment())
    }

    override fun onBackPressed() {
        activityRecruit2ViewModel.clearData()
        activity.onReplaceFragment(ActivityRecruitFragment())
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}