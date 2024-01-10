package com.letspl.oceankepper.ui.view

import android.os.Bundle
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.FragmentSettingBinding
import com.letspl.oceankepper.ui.dialog.ConnectFailedDialog
import com.letspl.oceankepper.ui.dialog.LogoutDialog
import com.letspl.oceankepper.ui.dialog.WithdrawDialog
import com.letspl.oceankepper.ui.viewmodel.LoginViewModel
import com.letspl.oceankepper.ui.viewmodel.SettingViewModel
import com.letspl.oceankepper.util.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.log

@AndroidEntryPoint
class SettingFragment : Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentSettingBinding? = null
    private val binding:FragmentSettingBinding get() = _binding!!
    private val activity:BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    private val settingViewModel: SettingViewModel by viewModels()

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
            if(it){
                activity.onReplaceFragment(LoginFragment(), false, false, 1)
            }
        }
        settingViewModel.postNotificationAlarmResult.observe(viewLifecycleOwner) {
            // 알림 설정 실패 시 실패 모달 표시
            it?.let {
                if(!it) {
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
            if(it != "") {
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
        when(EntryPoint.settingPoint) {
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
            settingViewModel.postLogout()
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