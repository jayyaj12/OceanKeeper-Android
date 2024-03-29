package com.letspl.oceankeeper.ui.view

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.letspl.oceankeeper.databinding.FragmentActivityApplyBinding
import com.letspl.oceankeeper.ui.dialog.RecruitActivityCompleteDialog
import com.letspl.oceankeeper.ui.viewmodel.ApplyActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ActivityApplyFragment() : Fragment(), BaseActivity.OnBackPressedListener {

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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModelObserver()
        getPrivacyContent()
        loadLastRecruitmentApplicationData()
    }

    // 마지막 지원서 불러오기
    private fun loadLastRecruitmentApplicationData() {
        applyActivityViewModel.getLastRecruitmentApplication()
    }

    // 개인정보 동의내용 불러오기
    private fun getPrivacyContent() {
        applyActivityViewModel.getPrivacyPolicy()
    }

    // 확인 버튼 클릭
    fun onClickedConfirm() {
        // 필수값이 모두 입력된 경우에만 활동 지원
        if (applyActivityViewModel.isInputNecessaryValue(
                binding.nameEt.text.toString(),
                binding.phonenumberEt.text.toString(),
                binding.emailEt.text.toString()
            )
        ) {
            Timber.e("test")
            // 개인정보 동의시에만 활동 지원 가능
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.text.toString())
                    .matches()
            ) {
                if (applyActivityViewModel.isPhoneNumberInt(binding.phonenumberEt.text.toString())) {
                    if (applyActivityViewModel.privacyAgreement.value == true) {
                        applyActivityViewModel.postApplyActivity(
                            binding.birthdayEt.text.toString(),
                            binding.emailEt.text.toString(),
                            binding.id1365Et.text.toString(),
                            binding.nameEt.text.toString(),
                            binding.phonenumberEt.text.toString(),
                            binding.askRecruitKeeperEt.text.toString(),
                            binding.startLocationEt.text.toString()
                        )
                    } else {
                        activity.showErrorMsg("개인정보 동의 후에 지원이 가능합니다.")
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupViewModelObserver() {

        applyActivityViewModel.getPrivacyResult.observe(viewLifecycleOwner) {
            binding.privacyTextTv.text = Html.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        applyActivityViewModel.getLastRecruitmentApplicationResult.observe(viewLifecycleOwner) {
            if(it != null) {
                binding.nameEt.setText(it.name) // 이름
                binding.emailEt.setText(it.email) // 이메일
                binding.id1365Et.setText(it.id1365) // 1365아이디
                binding.birthdayEt.setText(it.dayOfBirth) // 생년월일
                binding.phonenumberEt.setText(it.phoneNumber) // 연락처
                binding.startLocationEt.setText(it.startPoint) // 출발 지역명
                binding.askRecruitKeeperEt.setText(it.question)
                applyActivityViewModel.onClickedTransport(applyActivityViewModel.getClickedTransportStringToInt(it.transportation))
            }
        }
        applyActivityViewModel.errorMsg.observe(viewLifecycleOwner) {
            if(it != "") {
                activity.showErrorMsg(it)
            }
        }

        applyActivityViewModel.applyResult.observe(viewLifecycleOwner) {
            val dialog = RecruitActivityCompleteDialog(requireContext(),
                "활동 지원 신청 완료",
                applyActivityViewModel.getApplyActivityStartDate(),
                {
                    // 나의 활동 확인하기
                    activity.onReplaceFragment(MyActivityFragment(), false, true)
                },
                {
                    // 확인 버튼
                    activity.onReplaceFragment(null, false, true, 1)
                })

            dialog.setCancelable(false)
            dialog.show()
        }
    }

    fun onBackBtnClicked() {
        activity.onReplaceFragment(MainFragment(), false, true)
    }

    override fun onDestroyView() {
        _binding = null

        applyActivityViewModel.clearErrorMsg()
        super.onDestroyView()
    }

    override fun onBackPressed() {
        activity.onReplaceFragment(MainFragment(), false, true)
    }
}