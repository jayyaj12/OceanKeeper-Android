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
import com.letspl.oceankepper.databinding.FragmentJoinCompleteBinding
import com.letspl.oceankepper.ui.dialog.ChoiceProfileImageDialog
import com.letspl.oceankepper.ui.viewmodel.JoinViewModel
import com.letspl.oceankepper.ui.viewmodel.LoginViewModel
import com.letspl.oceankepper.util.BaseUrlType
import com.letspl.oceankepper.util.loginManager.NaverLoginManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject


@AndroidEntryPoint
class JoinCompleteFragment: Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentJoinCompleteBinding? = null
    private val binding: FragmentJoinCompleteBinding get() = _binding!!
    val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentJoinCompleteBinding.inflate(layoutInflater)
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

        // 확인 버튼 클릭
        binding.okBtn.setOnClickListener {
            activity.onReplaceFragment(MainFragment(), false, true)
        }
    }
    override fun onBackPressed() {
        activity.finish()
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }
}