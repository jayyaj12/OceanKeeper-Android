package com.letspl.oceankepper.ui.view

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
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
import com.letspl.oceankepper.databinding.FragmentEditActivityRecruit2Binding
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
import java.security.AccessController.checkPermission
import javax.inject.Inject

@AndroidEntryPoint
class EditActivityRecruit2Fragment(private val activityId: String) : Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentEditActivityRecruit2Binding? = null
    private val binding: FragmentEditActivityRecruit2Binding get() = _binding!!
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
        _binding = FragmentEditActivityRecruit2Binding.inflate(layoutInflater)
        binding.editActivityRecruit2Fragment = this
        binding.activityRecruit2ViewModel = activityRecruit2ViewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLoadData()
        setupEditTextListener()
        setupViewModelObserver()
    }

    // 데이터 불러오기
    private fun setupLoadData() {
        Glide.with(requireActivity()).load(activityRecruitViewModel.getThumbnailImgStr()).fitCenter()
            .into(binding.thumbnailIv)
        Glide.with(requireActivity()).load(activityRecruitViewModel.getKeeperIntroduceStr()).fitCenter()
            .into(binding.introduceKeeperIv)
        Glide.with(requireActivity()).load(activityRecruitViewModel.getActivityStoryImgStr()).fitCenter()
            .into(binding.activityStoryIv)

        binding.introduceKeeperEt.setText(activityRecruitViewModel.getKeeperIntroduceContent())
        binding.activityStoryEt.setText(activityRecruitViewModel.getActivityStoryContent())

        activityRecruit2ViewModel.onChangedKeeperIntroduceEditText(activityRecruitViewModel.getKeeperIntroduceContent())
        activityRecruit2ViewModel.onChangedActivityStoryEditText(activityRecruitViewModel.getActivityStoryContent())

        if(activityRecruitViewModel.getThumbnailImgStr() != "") {
            binding.thumbnailPhotoCl.setBackgroundResource(R.drawable.custom_radius_8_stroke_g300_solid_fff)
            binding.thumbnailPhotoTv.visibility = View.GONE
        }

        if(activityRecruitViewModel.getKeeperIntroduceStr() != "") {
            binding.introduceKeeperPhotoCl.setBackgroundResource(R.drawable.custom_radius_8_stroke_g300_solid_fff)
            binding.introduceKeeperPhotoTv.visibility = View.GONE
        }

        if(activityRecruitViewModel.getActivityStoryImgStr() != "") {
            binding.activityStoryPhotoCl.setBackgroundResource(R.drawable.custom_radius_8_stroke_g300_solid_fff)
            binding.activityStoryPhotoTv.visibility = View.GONE
        }
    }

    private fun setupViewModelObserver() {
        activityRecruit2ViewModel.errorMsg.observe(viewLifecycleOwner) {
            if(it != "") {
                activity.showErrorMsg(it)
            }
        }

        // 활동 모집 수정 성공 여부
        activityRecruit2ViewModel.editRecruitActivityIsSuccess.observe(viewLifecycleOwner) {
            if(it) {
                val dialog = RecruitActivityCompleteDialog(requireContext(),
                    "활동 모집 수정 완료",
                    activityRecruit2ViewModel.getRecruitEditCompleteText(),
                    {
                        // 나의 활동 확인하기
                        activity.onReplaceFragment(MyActivityFragment(), false, true)
                    },
                    {
                        // 확인 버튼
                        activity.onReplaceFragment(MainFragment(), false, true, 1)
                    })

                dialog.setCancelable(false)
                dialog.show()
                
                activityRecruitViewModel.clearTempData()
                activityRecruit2ViewModel.clearData()
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
        val imagePermission = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES
        )

        return if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
            Timber.e("true")
            if(imagePermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    ), REQ_GALLERY
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
                    ), REQ_GALLERY
                )
                false
            } else {
                true
            }
        }
    }

    fun selectThumbnailGallery() {
        if (checkGalleryPermission()) {
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
        if (activityRecruit2ViewModel.isEditExistNeedData()) {
            activityRecruit2ViewModel.patchActivityRegister(
                activityId = activityId,
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
        activity.onReplaceFragment(EditActivityRecruitFragment(activityId))
    }

    override fun onBackPressed() {
        activityRecruit2ViewModel.clearData()
        activity.onReplaceFragment(EditActivityRecruitFragment(activityId))
    }

    override fun onDestroyView() {
        _binding = null
        activityRecruit2ViewModel.clearErrorMsg()

        super.onDestroyView()
    }

}