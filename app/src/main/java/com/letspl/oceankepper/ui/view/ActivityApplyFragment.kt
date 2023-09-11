package com.letspl.oceankepper.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.letspl.oceankepper.databinding.FragmentActivityApplyBinding
import com.letspl.oceankepper.ui.viewmodel.ApplyActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityApplyFragment: Fragment(), BaseActivity.OnBackPressedListener {

    private var _binding: FragmentActivityApplyBinding? = null
    private val binding: FragmentActivityApplyBinding get() = _binding!!
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    private val applyActivityViewModel: ApplyActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = FragmentActivityApplyBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.applyActivityFragment = this
        binding.applyActivityViewModel = applyActivityViewModel
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
    }

    // 확인 버튼 클릭
    fun onClickedConfirm() {
        // 필수값이 모두 입력된 경우에만 활동 지원
        if(applyActivityViewModel.isInputNecessaryValue(binding.nameEt.text.toString(), binding.phonenumberEt.text.toString(), binding.emailEt.text.toString())) {
            // 개인정보 동의시에만 활동 지원 가능
            if(android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.text.toString()).matches()) {
                if(applyActivityViewModel.isPhoneNumberInt(binding.emailEt.text.toString())) {
                    if (applyActivityViewModel.privacyAgreement.value == true) {
                        applyActivityViewModel.postApplyActivity(
                            binding.birthdayEt.text.toString(),
                            binding.emailEt.text.toString(),
                            binding.id1365Et.text.toString(),
                            binding.nameEt.text.toString(),
                            binding.phonenumberEt.text.toString(),
                            true,
                            binding.askRecruitKeeperEt.text.toString(),
                            binding.startLocationEt.text.toString(),
                            "대중교통"
                        )
                    }
                } else {
                    Toast.makeText(requireActivity(), "연락처 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireActivity(), "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireActivity(), "필수 값을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    fun onBackBtnClicked() {
        activity.onReplaceFragment(MainFragment(), false, true)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onBackPressed() {
        activity.onReplaceFragment(MainFragment(), false, true)
    }
}