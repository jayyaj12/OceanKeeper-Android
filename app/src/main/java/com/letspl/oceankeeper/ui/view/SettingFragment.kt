package com.letspl.oceankeeper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.letspl.oceankeeper.databinding.FragmentSettingBinding
import com.letspl.oceankeeper.ui.dialog.ConnectFailedDialog
import com.letspl.oceankeeper.ui.dialog.LogoutDialog
import com.letspl.oceankeeper.ui.dialog.WithdrawDialog
import com.letspl.oceankeeper.ui.viewmodel.SettingViewModel
import com.letspl.oceankeeper.util.EntryPoint
import com.letspl.oceankeeper.util.loginManager.KakaoLoginManager
import com.letspl.oceankeeper.util.loginManager.NaverLoginManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentSettingBinding? = null
    private val binding: FragmentSettingBinding get() = _binding!!
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    private val settingViewModel: SettingViewModel by viewModels()

    @Inject
    lateinit var naverLoginManager: NaverLoginManager

    @Inject
    lateinit var kakaoLoginManager: KakaoLoginManager

    override fun onBackPressed() {
        onClickedBackBtn()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(layoutInflater)
        binding.settingFragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModelObserver()
        setupNotificationSwitch()
        getNotificationState()
    }

    // 알림 설정 가져오기
    private fun getNotificationState() {
        settingViewModel.getNotificationAlarm()
    }

    private fun setupViewModelObserver() {
        settingViewModel.postLogoutResult.observe(viewLifecycleOwner) {
            if (it) {
                activity.onReplaceFragment(LoginFragment(), false, false)
            }
        }
        settingViewModel.postNotificationAlarmResult.observe(viewLifecycleOwner) {
            // 알림 설정 실패 시 실패 모달 표시
            it?.let {
                if (!it) {
                    ConnectFailedDialog(requireContext()).show()
                }
            }
        }
        settingViewModel.getNotificationAlarmResult.observe(viewLifecycleOwner) {
            Timber.e("getNotificationAlarmResult $it")
            // 알림 설정 실패 시 실패 모달 표시
            it?.let {
                binding.notiSwitch.isChecked = it
            }
        }
        settingViewModel.errorMsg.observe(viewLifecycleOwner) {
            if (it != "") {
                activity.showErrorMsg(it)
            }
        }
    }

    // 알림 설정
    private fun setupNotificationSwitch() {
        binding.notiSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            Timber.e("setOnCheckedChangeListener $isChecked")
            settingViewModel.postNotificationAlarm(isChecked)
        }
    }

    // 뒤로가기 버튼
    fun onClickedBackBtn() {
        when (EntryPoint.settingPoint) {
            "main" -> activity.onReplaceFragment(MainFragment(), false, true)
            "message" -> activity.onReplaceFragment(MessageFragment(), false, true)
            "myActivity" -> activity.onReplaceFragment(MyActivityFragment(), false, true)
        }
    }

    // 공지사항
    fun onMoveNoticeFragment() {
        EntryPoint.noticePoint = "setting"
        activity.onReplaceFragment(NoticeFragment(), false, false)
    }

    // 이용약관
    fun onMoveRuleFragment() {
        activity.onReplaceFragment(RuleFragment(), false, false)
    }

    // 로그아웃
    fun onClickedLogout() {
        LogoutDialog(requireContext()) {
            var logoutResult = true
            lifecycleScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.IO) {
                    when (EntryPoint.login) {
                        "NAVER" -> naverLoginManager.startNaverLogout()
                        "KAKAO" -> logoutResult = kakaoLoginManager.startLogout()
                        "APPLE" -> false
                        else -> logoutResult = false
                    }
                }

                if(logoutResult) {
                    settingViewModel.postLogout()
                } else {
                    activity.showErrorMsg("로그아웃에 실패하였습니다.\n잠시 후 다시 시도해주세요.")
                }
            }

        }.show()
    }

    // 탈퇴하기
    fun onClickedWithdraw() {
        WithdrawDialog(requireContext()) {
            settingViewModel.postWithdraw()
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingViewModel.clearLiveData()
        settingViewModel.clearErrorMsg()

        _binding = null
    }

}