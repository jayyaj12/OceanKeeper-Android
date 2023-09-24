package com.letspl.oceankepper.ui.view

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.letspl.oceankepper.databinding.FragmentJoinBinding
import com.letspl.oceankepper.ui.dialog.ChoiceProfileImageDialog
import com.letspl.oceankepper.ui.viewmodel.JoinViewModel
import com.letspl.oceankepper.ui.viewmodel.LoginViewModel
import com.letspl.oceankepper.util.BaseUrlType
import com.letspl.oceankepper.util.ResizingImage
import com.letspl.oceankepper.util.loginManager.NaverLoginManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject


@AndroidEntryPoint
class JoinFragment: Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentJoinBinding? = null
    private val binding: FragmentJoinBinding get() = _binding!!
    val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    @Inject lateinit var naverLoginManager: NaverLoginManager
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var choiceProfileImageDialog:  ChoiceProfileImageDialog
    private val joinViewModel: JoinViewModel by viewModels()
    private val permissionList = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val resizingImage = ResizingImage()

    // 사진 찍기 결과
    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        Glide.with(requireContext())
            .load(joinViewModel.getTakePhotoUri())
            .fitCenter()
            .into(binding.profileIv)

        joinViewModel.setProfileImageFile(it?.let { uri ->
            resizingImage.convertResizeImage(requireContext(),
                joinViewModel.getTakePhotoUri()!!
            )
        })
    }
    // 사진 가져오기 결과
    private val choicePhoto = registerForActivityResult(ActivityResultContracts.GetContent()) {
        Glide.with(requireContext())
            .load(it)
            .fitCenter()
            .into(binding.profileIv)

        // 이미지 파일 저장
        joinViewModel.setProfileImageFile(it?.let { uri ->
            resizingImage.convertResizeImage(requireContext(),
                uri
            )
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentJoinBinding.inflate(layoutInflater)
        binding.joinFragment = this
        binding.joinViewModel = joinViewModel
        binding.lifecycleOwner = this
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
        requestPermission()
        setupChoiceProfileImageDialog()

        Glide.with(requireContext())
            .load(loginViewModel.getLoginInfo().profile)
            .into(binding.profileIv)

        binding.nicknameEt.setText(loginViewModel.getLoginInfo().nickname)
        setupViewModelObserver()

        joinViewModel.createProfileImageFile()
    }

    private fun setupViewModelObserver() {
        joinViewModel.signUpResult.observe(viewLifecycleOwner) {
            if(it) {
                activity.onReplaceFragment(JoinCompleteFragment())
            } else {
                Toast.makeText(requireContext(), "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
        }

        joinViewModel.profileTempFileCreated.observe(viewLifecycleOwner) {
            joinViewModel.setProfileImageFile(
                resizingImage.convertResizeImage(requireContext(),
                    Uri.fromFile(it)
                )
            )
        }
    }

    // 필요한 권한 요청
    private fun requestPermission() {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.forEach {
                if(it.value) {
                    Toast.makeText(requireContext(), "권한 허용 필요", Toast.LENGTH_SHORT).show()
                }
            }
        }.launch(permissionList)
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
        return requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content)
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

        super.onDestroyView()
    }
}