package com.letspl.oceankeeper.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.letspl.oceankeeper.databinding.FragmentEditActivityApplyBinding
import com.letspl.oceankeeper.ui.dialog.RecruitActivityCompleteDialog
import com.letspl.oceankeeper.ui.viewmodel.ApplyActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditActivityApplyFragment(private val applicationId: String) : Fragment(), BaseActivity.OnBackPressedListener {

    private var _binding: FragmentEditActivityApplyBinding? = null
    private val binding: FragmentEditActivityApplyBinding get() = _binding!!
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    private val applyActivityViewModel: ApplyActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = FragmentEditActivityApplyBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.editApplyActivityFragment = this
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

        setupViewModelObserver()
        loadData()
    }

    // 기존 활동 지원서 데이터 불러오기
    private fun loadData() {
        applyActivityViewModel.getDetailApplication(applicationId)
    }

    // 확인 버튼 클릭
    fun onClickedEdit() {
        // 필수값이 모두 입력된 경우에만 활동 지원
        if (applyActivityViewModel.isInputNecessaryValue(
                binding.nameEt.text.toString(),
                binding.phonenumberEt.text.toString(),
                binding.emailEt.text.toString()
            )
        ) {
            // 개인정보 동의시에만 활동 지원 가능
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.text.toString())
                    .matches()
            ) {
                if (applyActivityViewModel.isPhoneNumberInt(binding.phonenumberEt.text.toString())) {
                    if (applyActivityViewModel.privacyAgreement.value == true) {
                        applyActivityViewModel.patchApplication(
                            applicationId,
                            binding.birthdayEt.text.toString(),
                            binding.emailEt.text.toString(),
                            binding.id1365Et.text.toString(),
                            binding.nameEt.text.toString(),
                            binding.phonenumberEt.text.toString(),
                            binding.askRecruitKeeperEt.text.toString(),
                            binding.startLocationEt.text.toString()
                        )
                    }
                } else {
                    activity.showErrorMsg("연락처 형식이 올바르지 않습니다.")
                }
            } else {
                activity.showErrorMsg("이메일 형식이 올바르지 않습니다.")
            }
        } else {
            activity.showErrorMsg("필수 값을 모두 입력해주세요.")
        }
    }

    private fun setupViewModelObserver() {
        applyActivityViewModel.errorMsg.observe(viewLifecycleOwner) {
            if(it != "") {
                activity.showErrorMsg(it)
            }
        }

        applyActivityViewModel.getDetailApplication.observe(viewLifecycleOwner) {
            it.run {
                binding.nameEt.setText(this.name)
                binding.phonenumberEt.setText(this.phoneNumber)
                binding.id1365Et.setText(this.id1365)
                binding.birthdayEt.setText(this.dayOfBirth.toString())
                binding.emailEt.setText(this.email)
                binding.startLocationEt.setText(this.startPoint)
                applyActivityViewModel.onClickedTransport(applyActivityViewModel.getClickedTransportStringToInt(this.transportation))
                binding.askRecruitKeeperEt.setText(this.question)
                applyActivityViewModel.onClickedPrivacyAgreement()
            }
        }

        applyActivityViewModel.editApplyResult.observe(viewLifecycleOwner) {
            val dialog = RecruitActivityCompleteDialog(requireContext(),
                "활동 신청 수정 완료",
                it,
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
        }
    }

    fun onBackBtnClicked() {
        activity.onReplaceFragment(MainFragment(), false, true, 1)
    }

    override fun onDestroyView() {
        _binding = null
        applyActivityViewModel.clearErrorMsg()

        super.onDestroyView()
    }

    override fun onBackPressed() {
        activity.onReplaceFragment(MainFragment(), false, true, 1)
    }
}