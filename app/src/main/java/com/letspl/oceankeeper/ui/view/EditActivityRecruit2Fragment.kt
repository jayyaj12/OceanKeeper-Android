package com.letspl.oceankeeper.ui.view

import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.letspl.oceankeeper.R
import com.letspl.oceankeeper.databinding.FragmentEditActivityRecruit2Binding
import com.letspl.oceankeeper.ui.dialog.ProgressDialog
import com.letspl.oceankeeper.ui.dialog.RecruitActivityCompleteDialog
import com.letspl.oceankeeper.ui.viewmodel.ActivityRecruit2ViewModel
import com.letspl.oceankeeper.ui.viewmodel.ActivityRecruitViewModel
import com.letspl.oceankeeper.util.ImgFileMaker
import com.letspl.oceankeeper.util.ResizingImage
import com.letspl.oceankeeper.util.RotateTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
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
    lateinit var progressDialog: ProgressDialog

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

                        val imgFile = ImgFileMaker.saveBitmapToFile(
                            rotateBitmap!!
                        )

                        activityRecruit2ViewModel.setThumbnailImageFile(
                            imgFile
                        )

                        binding.thumbnailIv.visibility = View.VISIBLE
                        Glide.with(requireActivity()).load(imgFile).into(binding.thumbnailIv)

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

                        val imgFile = ImgFileMaker.saveBitmapToFile(
                            rotateBitmap!!
                        )

                        activityRecruit2ViewModel.setKeeperIntroduceImageFile(
                            imgFile
                        )

                        binding.introduceKeeperIv.visibility = View.VISIBLE
                        Glide.with(requireActivity()).load(imgFile).into(binding.introduceKeeperIv)

                        binding.introduceKeeperPhotoCl.setBackgroundResource(R.drawable.custom_radius_8_stroke_g300_solid_fff)
                        binding.introduceKeeperPhotoTv.visibility = View.GONE
                        binding.introduceKeeperInfoTv.visibility = View.GONE
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

                        val imgFile = ImgFileMaker.saveBitmapToFile(
                            rotateBitmap!!
                        )

                        activityRecruit2ViewModel.setActivityStoryImageFile(
                            imgFile
                        )

                        binding.activityStoryIv.visibility = View.VISIBLE
                        Glide.with(requireActivity()).load(imgFile).into(binding.activityStoryIv)

                        binding.activityStoryPhotoCl.setBackgroundResource(R.drawable.custom_radius_8_stroke_g300_solid_fff)
                        binding.activityStoryPhotoTv.visibility = View.GONE
                        binding.activityStoryInfoTv.visibility = View.GONE
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
        setupProgressDialog()
    }

    private fun setupProgressDialog() {
        progressDialog = ProgressDialog(requireContext())
    }

    // 데이터 불러오기
    private fun setupLoadData() {
        // 이미지 세팅
        Glide.with(requireActivity()).load(activityRecruitViewModel.getThumbnailImgStr()).fitCenter()
            .into(binding.thumbnailIv)

        // 키퍼 소개 이미지가 없을 경우 background 로 black 을 깔아뒀기 때문에 visble gone 되어 있음 이미지 있으면 배경 표시하기 위해 visble 로 변경
        if(activityRecruitViewModel.getKeeperIntroduceStr() != "") {
            binding.introduceKeeperIv.visibility = View.VISIBLE
            binding.introduceKeeperPhotoCl.setBackgroundResource(R.drawable.custom_radius_8_stroke_g300_solid_fff)
            Glide.with(requireActivity()).load(activityRecruitViewModel.getKeeperIntroduceStr()).fitCenter()
                .into(binding.introduceKeeperIv)
        }
        if(activityRecruitViewModel.getActivityStoryImgStr() != "") {
            binding.activityStoryIv.visibility = View.VISIBLE
            binding.activityStoryPhotoCl.setBackgroundResource(R.drawable.custom_radius_8_stroke_g300_solid_fff)
            Glide.with(requireActivity()).load(activityRecruitViewModel.getActivityStoryImgStr()).fitCenter()
                .into(binding.activityStoryIv)
        }

        // 키퍼 소개 text
        binding.introduceKeeperEt.setText(activityRecruitViewModel.getKeeperIntroduceContent())
        // 활동 스토리 text
        binding.activityStoryEt.setText(activityRecruitViewModel.getActivityStoryContent())
        // 키퍼 소개 text length
        activityRecruit2ViewModel.onChangedKeeperIntroduceEditText(activityRecruitViewModel.getKeeperIntroduceContent())
        // 활동 스토리 text length
        activityRecruit2ViewModel.onChangedActivityStoryEditText(activityRecruitViewModel.getActivityStoryContent())
    }

    private fun setupViewModelObserver() {
        activityRecruit2ViewModel.errorMsg.observe(viewLifecycleOwner) {
            if(it != "") {
                progressDialog.dismiss()
                activity.showErrorMsg(it)
            }
        }

        // 활동 모집 수정 성공 여부
        activityRecruit2ViewModel.editRecruitActivityIsSuccess.observe(viewLifecycleOwner) {
            Timber.e("editRecruitActivityIsSuccess ${activityRecruit2ViewModel.editRecruitActivityIsSuccess.value}")
            if(it) {
                progressDialog.dismiss()
                val dialog = RecruitActivityCompleteDialog(requireContext(),
                    "활동 모집 수정 완료",
                    activityRecruit2ViewModel.getRecruitEditCompleteText(),
                    {
                        // 나의 활동 확인하기
                        activity.onReplaceFragment(MyActivityFragment(), false, true)
                    },
                    {
                        Timber.e("check completion")
                        // 확인 버튼
                        activity.onReplaceFragment(null, false, true, 1)
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
            if(readPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(
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
            // 앨범에서 사진 선택
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }

            thumbnailImageResult.launch(intent)
        }
    }

    fun selectKeeperIntroduceGallery() {
        if (checkGalleryPermission()) {
            // 앨범에서 사진 선택
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }

            keeperIntroduceImageResult.launch(intent)
        }
    }

    fun selectActivityStoryGallery() {
        if (checkGalleryPermission()) {
            // 앨범에서 사진 선택
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }

            activityStoryImageResult.launch(intent)
        }
    }

    // 완료 버튼 클릭
    fun onClickedCompleteBtn() {
        if (activityRecruit2ViewModel.isEditExistNeedData()) {
            progressDialog.show()
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
            activity.showErrorMsg("모든 값을 입력해주세요.")
        }
    }

    // 뒤로가기 버튼 클릭
    fun onClickedBackBtn() {
        activityRecruit2ViewModel.clearData()
        activity.onReplaceFragment(EditActivityRecruitFragment(activityId))
    }

    override fun onBackPressed() {
        onClickedBackBtn()
    }

    override fun onDestroyView() {
        _binding = null
        activityRecruit2ViewModel.clearErrorMsg()

        super.onDestroyView()
    }

}