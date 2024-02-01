package com.letspl.oceankeeper.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.letspl.oceankeeper.databinding.FragmentNotificationBinding
import com.letspl.oceankeeper.ui.adapter.NotificationListAdapter
import com.letspl.oceankeeper.ui.viewmodel.SettingViewModel
import com.letspl.oceankeeper.util.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NotificationFragment : Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentNotificationBinding? = null
    private val binding: FragmentNotificationBinding get() = _binding!!
    private val settingViewModel: SettingViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    private lateinit var notificationListAdapter: NotificationListAdapter

    override fun onBackPressed() {
        onClickedCloseBtn()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(layoutInflater)
        binding.settingViewModel = settingViewModel
        binding.lifecycleOwner = this
        binding.notificationFragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNotificationListAdapter()
        setupViewModelObserver()
        settingViewModel.getNotificationList()
    }

    fun setupViewModelObserver() {
        settingViewModel.getNotificationListResult.observe(viewLifecycleOwner) {
            Timber.e("it $it")
            notificationListAdapter.submitList(it.toMutableList())
        }
        settingViewModel.errorMsg.observe(viewLifecycleOwner) {
            if (it != "") {
                activity.showErrorMsg(it)
            }
        }
    }

    private fun setupNotificationListAdapter() {
        notificationListAdapter = NotificationListAdapter()

        binding.notificationRv.adapter = notificationListAdapter
        binding.notificationRv.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (!binding.notificationRv.canScrollVertically(1)) {
                Timber.e("최상단")
                settingViewModel.getNotificationList()
            }
        }
    }

    fun onClickedCloseBtn() {
        when (EntryPoint.notificationPoint) {
            "main" -> activity.onReplaceFragment(MainFragment(), false, true)
            "message" -> activity.onReplaceFragment(MessageFragment(), false, true)
            "myActivity" -> activity.onReplaceFragment(MyActivityFragment(), false, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        settingViewModel.clearLiveData()

        _binding = null
    }
}