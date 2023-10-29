package com.letspl.oceankepper.ui.view

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
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
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.letspl.oceankepper.databinding.FragmentMyActivityBinding
import com.letspl.oceankepper.ui.dialog.ChoiceProfileImageDialog
import com.letspl.oceankepper.ui.viewmodel.LoginViewModel
import com.letspl.oceankepper.ui.viewmodel.MyActivityViewModel
import com.letspl.oceankepper.util.ImgFileMaker
import com.letspl.oceankepper.util.ResizingImage
import com.letspl.oceankepper.util.RotateTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
    private val loginViewModel: LoginViewModel by viewModels()
    private val myActivityViewModel: MyActivityViewModel by viewModels()
    private lateinit var choiceProfileImageDialog: ChoiceProfileImageDialog
    private val resizingImage = ResizingImage()

    // 사진 찍기 결과
    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                Glide.with(requireContext())
                    .load(myActivityViewModel.getTakePhotoUri())
                    .fitCenter()
                    .into(binding.userProfileIv)

                myActivityViewModel.setProfileImageFile(it?.let { uri ->
                    resizingImage.convertResizeImage(
                        requireContext(),
                        myActivityViewModel.getTakePhotoUri()!!
                    )
                })
            }
            Timber.e("uploadEditProfileImage1")

            withContext(Dispatchers.IO) {
//                myActivityViewModel.uploadEditProfileImage()

            }
        }
    }

    // 사진 가져오기 결과
    @RequiresApi(Build.VERSION_CODES.O)
    private val choicePhoto = registerForActivityResult(ActivityResultContracts.GetContent()) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                Glide.with(requireContext())
                    .load(it)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(binding.userProfileIv)

                val path = ImgFileMaker.getFullPathFromUri(requireContext(), it)!!
                val angle = RotateTransform.getRotationAngle(path)
                val rotateBitmap = RotateTransform.rotateImage(BitmapFactory.decodeFile(path), angle.toFloat())

                myActivityViewModel.uploadEditProfileImage(ImgFileMaker.saveBitmapToFile(rotateBitmap!!, path))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyActivityBinding.inflate(layoutInflater)
        binding.myActivityViewModel = myActivityViewModel
        binding.myActivityFragment = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabLayout()
        getActivityInfo()
        setupUserProfile()
        setupChoiceProfileImageDialog()
    }

    // 나의 오션키퍼 활동 정보 불러오기
    private fun getActivityInfo() {
        myActivityViewModel.getMyActivityInfo()
    }

    // 유저 프로필 표시
    private fun setupUserProfile() {
        Timber.e("profile ${myActivityViewModel.getUserProfile()}")

        Glide.with(requireContext())
            .load(myActivityViewModel.getUserProfile())
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .dontTransform()
            .into(binding.userProfileIv)
    }

    // 촬영, 앨범에서 선택 팝업 세팅
    private fun setupChoiceProfileImageDialog() {
        choiceProfileImageDialog = ChoiceProfileImageDialog(requireContext(), {
            // 사진 촬영
            myActivityViewModel.setTakePhotoUri(createImageFile())
            takePhoto.launch(myActivityViewModel.getTakePhotoUri())
        }, {
            // 앨범에서 사진 선택
            choicePhoto.launch("image/*")
        })
    }

    // temp 이미지 파일 생성
    private fun createImageFile(): Uri? {
        val now = SimpleDateFormat("yyMMdd_HHmmss").format(Date())
        val content = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "img_$now.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        }
        return requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content)
    }

    // 프로필 수정 아이콘 클릭 시 촬영, 앨범에서 선택 팝업 표시
    fun onClickedChoiceProfileImage() {
        choiceProfileImageDialog.show()
    }

    // tablayout 세팅
    private fun setupTabLayout() {
        binding.itemTab.setTabTextColors(Color.parseColor("#7a7a7a"), Color.parseColor("#545454"))
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}