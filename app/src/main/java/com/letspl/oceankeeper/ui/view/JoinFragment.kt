package com.letspl.oceankeeper.ui.view

import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.letspl.oceankeeper.R
import com.letspl.oceankeeper.databinding.FragmentJoinBinding
import com.letspl.oceankeeper.ui.dialog.ChoiceProfileImageDialog
import com.letspl.oceankeeper.ui.viewmodel.JoinViewModel
import com.letspl.oceankeeper.ui.viewmodel.LoginViewModel
import com.letspl.oceankeeper.util.BaseUrlType
import com.letspl.oceankeeper.util.ImgFileMaker
import com.letspl.oceankeeper.util.ResizingImage
import com.letspl.oceankeeper.util.RotateTransform
import com.letspl.oceankeeper.util.loginManager.NaverLoginManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject


@AndroidEntryPoint
class JoinFragment : Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentJoinBinding? = null
    private val binding: FragmentJoinBinding get() = _binding!!
    val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    @Inject
    lateinit var naverLoginManager: NaverLoginManager
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var choiceProfileImageDialog: ChoiceProfileImageDialog
    private val joinViewModel: JoinViewModel by viewModels()
    private val resizingImage = ResizingImage()

    // 사진 찍기 결과
    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        try {
            val path =
                ImgFileMaker.getFullPathFromUri(requireContext(), joinViewModel.getTakePhotoUri())!!
            val angle = RotateTransform.getRotationAngle(path)
            val rotateBitmap =
                RotateTransform.rotateImage(BitmapFactory.decodeFile(path), angle.toFloat(), joinViewModel.getTakePhotoUri())

            joinViewModel.setProfileImageFile(it?.let { uri ->
                ImgFileMaker.saveBitmapToFile(rotateBitmap!!, path)
            })

            Glide.with(requireContext())
                .load(ImgFileMaker.saveBitmapToFile(rotateBitmap!!, path))
                .fitCenter()
                .centerCrop()
                .into(binding.profileIv)
        } catch (e: Exception) {
            Timber.e("e.getMessage ${e.message}")
            activity.showErrorMsg("해당 이미지는 사용할 수 없습니다.")
        }
    }

    // 사진 가져오기 결과
    private val choicePhoto = registerForActivityResult(ActivityResultContracts.GetContent()) {
        try {
            val path =
                ImgFileMaker.getFullPathFromUri(requireContext(), it)!!
            val angle = RotateTransform.getRotationAngle(path)
            val rotateBitmap =
                RotateTransform.rotateImage(BitmapFactory.decodeFile(path), angle.toFloat(), it)

            joinViewModel.setProfileImageFile(it?.let { uri ->
                ImgFileMaker.saveBitmapToFile(rotateBitmap!!, path)
            })

            Glide.with(requireContext())
                .load(ImgFileMaker.saveBitmapToFile(rotateBitmap!!, path))
                .fitCenter()
                .centerCrop()
                .into(binding.profileIv)
        } catch (e: Exception) {
            Timber.e("e.getMessage ${e.message}")
            activity.showErrorMsg("해당 이미지는 사용할 수 없습니다.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentJoinBinding.inflate(layoutInflater)
        binding.joinFragment = this
        binding.joinViewModel = joinViewModel
        binding.lifecycleOwner = this

        binding.nicknameEt.addTextChangedListener {
            joinViewModel.setLoginNickname(it.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BaseUrlType.setBaseUrlType("naver")
        checkGalleryPermission()
        setupChoiceProfileImageDialog()

        binding.nicknameEt.setText(loginViewModel.getLoginInfo().nickname)
        setupViewModelObserver()

        if(loginViewModel.getLoginInfo().profile != "") {
            Glide.with(requireContext())
                .load(loginViewModel.getLoginInfo().profile)
                .into(binding.profileIv)

            joinViewModel.createProfileImageFile()
        } else {
            joinViewModel.makeTempProfileFile()
        }
    }

    private fun setupViewModelObserver() {
        joinViewModel.errorMsg.observe(viewLifecycleOwner) {
            if(it != "") {
                activity.showErrorMsg(it)
            }
        }

        joinViewModel.signUpResult.observe(viewLifecycleOwner) {
            activity.onReplaceFragment(JoinCompleteFragment())
        }

        joinViewModel.profileTempFileCreated.observe(viewLifecycleOwner) {
            joinViewModel.setProfileImageFile(
                resizingImage.convertResizeImage(
                    requireContext(),
                    Uri.fromFile(it)
                )
            )
        }
    }

    // 필요한 권한 요청
//    private fun requestPermission() {
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
//            result.forEach {
//                if (it.value) {
//                    Toast.makeText(requireContext(), "권한 허용 필요", Toast.LENGTH_SHORT).show()
//
//                }
//            }
//        }.launch(permissionList)
//    }

    private fun checkGalleryPermission(): Boolean {
        val imagePermission = ContextCompat.checkSelfPermission(
            requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES
        )
        return if (imagePermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    android.Manifest.permission.READ_MEDIA_IMAGES
                ), 2
            )

            false
        } else {
            true
        }
    }

    // 프로필 사진 선택 모달 표시
    private fun setupChoiceProfileImageDialog() {
        choiceProfileImageDialog = ChoiceProfileImageDialog(requireContext(), {
            joinViewModel.setTakePhotoUri(createImageFile())
            // 사진 촬영
            takePhoto.launch(joinViewModel.getTakePhotoUri())
        }, {
            // 앨범에서 사진 선택
            choicePhoto.launch("image/*")
        })
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        if (contentUri.path!!.startsWith("/storage")) {
            return contentUri.path
        }
        val id = DocumentsContract.getDocumentId(contentUri).split(":".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()[1]
        val columns = arrayOf(MediaStore.Files.FileColumns.DATA)
        val selection = MediaStore.Files.FileColumns._ID + " = " + id
        val cursor: Cursor = requireActivity().contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            columns,
            selection,
            null,
            null
        )!!
        try {
            val columnIndex = cursor.getColumnIndex(columns[0])
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor.close()
        }
        return null
    }

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

    fun onClickedChoiceProfileImage() {
        choiceProfileImageDialog.show()
    }

    fun onClickedBackIcon() {
        activity.onReplaceFragment(LoginFragment())
    }

    override fun onBackPressed() {
        activity.onReplaceFragment(LoginFragment())
    }

    override fun onDestroyView() {
        _binding = null
        loginViewModel.clearErrorMsg()
        joinViewModel.clearErrorMsg()

        super.onDestroyView()
    }
}