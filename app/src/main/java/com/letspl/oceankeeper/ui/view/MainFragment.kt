package com.letspl.oceankeeper.ui.view

import MarginItemDecoration
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.letspl.oceankeeper.data.dto.ComingScheduleItem
import com.letspl.oceankeeper.data.model.MainModel
import com.letspl.oceankeeper.databinding.FragmentMainBinding
import com.letspl.oceankeeper.ui.adapter.MainActivityListAdapter
import com.letspl.oceankeeper.ui.adapter.MainComingScheduleAdapter
import com.letspl.oceankeeper.ui.dialog.AreaChoiceDialog
import com.letspl.oceankeeper.ui.dialog.GarbageCategoryChoiceDialog
import com.letspl.oceankeeper.ui.viewmodel.MainViewModel
import com.letspl.oceankeeper.util.EntryPoint
import com.letspl.oceankeeper.util.Extension.fromDpToPx
import com.letspl.oceankeeper.util.GridSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


@AndroidEntryPoint
class MainFragment: Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }
    private lateinit var activityListAdapter: MainActivityListAdapter
    private lateinit var comingScheduleAdapte: MainComingScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentMainBinding.inflate(layoutInflater)
        binding.mainViewModel = mainViewModel
        binding.mainFragment = this
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

        Timber.e("asdadadadadd")

        setScreenHeightSize()
        setUpViewModelObservers()
        setupFixScrollView()
        setupRecyclerview()
        loadData()
    }

    private fun setScreenHeightSize() {
        val display = requireActivity().windowManager.defaultDisplay // in case of Activity
        /* val display = activity!!.windowManaver.defaultDisplay */ // in case of Fragment
        val size = Point()
        display.getSize(size) // or getSize(size)
        MainModel.screenHeightSize =  size.y
    }

    // 스크롤 중 탭 영역은 상단에 고정함
    private fun setupFixScrollView() {
        binding.mainScrollview.run {
            header = binding.fixView
            stickListener = {
                Timber.e("stickListener")
            }
            freeListener = {
                Timber.e("freeListener")
            }
        }
    }

    // viewModel 옵저버 세팅
    private fun setUpViewModelObservers() {
        mainViewModel.errorMsg.observe(viewLifecycleOwner) {
            if(it != "") {
                activity.showErrorMsg(it)
            }
        }

        // 다가오는 일정 조회 완료되면 viewpager 자동 슬라이드 setup
        mainViewModel.getComingScheduleResult.observe(viewLifecycleOwner) {
            Timber.e("getComingScheduleResult")
            setupViewPager2(it)
        }

        // 내 활동 조회 결과 표시
        mainViewModel.getMyActivityResult.observe(viewLifecycleOwner) {
            Timber.e("getMyActivityResult $it")
            activityListAdapter.submitList(it.toMutableList())
        }

        // 활동 진행 상태 변경 시 활동을 재조회 한다.
        mainViewModel.activityStatusPosition.observe(viewLifecycleOwner) {
            mainViewModel.clearActivityList()
            mainViewModel.setLastActivity(false)
            mainViewModel.initGarbageLocationSelected()
            mainViewModel.getMyActivities(mainViewModel.getGarbageCategoryModalClickWordEng(), mainViewModel.getAreaModalClickWordEng(), 10, mainViewModel.getActivityStatus()) // 내 활동 조회
        }

        // 지역 모달 값 변경 시 텍스트를 변경해준다.
        mainViewModel.areaModalClickPosition.observe(viewLifecycleOwner) {
            binding.areaTv.text = mainViewModel.getAreaModalClickWordKor()
        }

        // 종류 모달 값 변경 시 텍스트를 변경해준다.
        mainViewModel.garbageCategoryModalClickPosition.observe(viewLifecycleOwner) {
            binding.kindTv.text = mainViewModel.getGarbageCategoryModalClickWordKor()
        }
    }

    // 데이터 불러오기
    private fun loadData() {
        mainViewModel.getComingSchedule() // 다가오는 일정 조회
    }

    fun onClickedAreaChoice() {
        AreaChoiceDialog(requireContext(), mainViewModel, viewLifecycleOwner) {
            mainViewModel.clearActivityList()
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    mainViewModel.setLastActivity(false)
                    mainViewModel.saveAreaModalClickPosition()
                }
                mainViewModel.getMyActivities(
                    mainViewModel.getGarbageCategoryModalClickWordEng(),
                    mainViewModel.getAreaModalClickWordEng(),
                    4,
                    mainViewModel.getActivityStatus()
                ) // 내 활동 조회
            }
        }.show()
    }

    fun onClickedGarbageCategoryChoice() {
        GarbageCategoryChoiceDialog(requireContext(), mainViewModel, viewLifecycleOwner) {
            mainViewModel.clearActivityList()
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    mainViewModel.setLastActivity(false)
                    mainViewModel.saveGarbageCategoryModalClickPosition()
                }
                mainViewModel.getMyActivities(mainViewModel.getGarbageCategoryModalClickWordEng(), mainViewModel.getAreaModalClickWordEng(), 4, mainViewModel.getActivityStatus()) // 내 활동 조회
            }
        }.show()
    }

    // 자동 슬라이드 구현(viewpager2)
    private fun setupViewPager2(list: List<ComingScheduleItem>) {
        comingScheduleAdapte = MainComingScheduleAdapter(list) { activityId ->
            mainViewModel.setClickedActivityId(activityId)
            EntryPoint.activityDetail = "main"
            activity.onReplaceFragment(ActivityDetailFragment(), false, false)
        }

        binding.scheduleViewPager.apply {
            clipToPadding = false
            clipChildren = false
            adapter = comingScheduleAdapte
            addItemDecoration(
                // 슬라이드간 마진 간격
                MarginItemDecoration(17)
            )
            registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // 몇번째께 선택됐냐에 따라 다름
                    Timber.e("position ${position}")
                    mainViewModel.onChangeSlidePosition(position)
                }
            })
        }
    }

    fun onMoveRecruitActivity() {
        mainViewModel.clearActivityList()
        activity.onReplaceFragment(ActivityRecruitFragment(), false, false)
    }

    fun onMoveGuide() {
        activity.onReplaceFragment(GuideFragment(), false, false)
    }

    fun onMoveSetting() {
        EntryPoint.settingPoint = "main"
        activity.onReplaceFragment(SettingFragment(), false, false)
    }

    fun onMoveNotification() {
        EntryPoint.notificationPoint = "main"
        activity.onReplaceFragment(NotificationFragment(), false, false)
    }

    fun onMoveNotice() {
        EntryPoint.noticePoint = "main"
        activity.onReplaceFragment(NoticeFragment(), false, false)
    }

    // recyclerview 세팅
    private fun setupRecyclerview() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.activityRv.run {
            layoutManager = gridLayoutManager
            addItemDecoration(
                GridSpacingItemDecoration(spanCount = 2, spacing = 16f.fromDpToPx())
            )
            itemAnimator = null
        }

        // 선택될 경우 activityId 값을 저장 후 상세 페이지로 이동
        activityListAdapter = MainActivityListAdapter(requireContext()) {
            EntryPoint.activityDetail = "main"
            mainViewModel.setClickedActivityId(it)
            activity.onReplaceFragment(ActivityDetailFragment(), false, false)
            mainViewModel.clearActivityList()
        }

        binding.activityRv.adapter = activityListAdapter

        binding.mainScrollview.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(!binding.mainScrollview.canScrollVertically(1)) {
                Timber.e("최하단")
                mainViewModel.getMyActivities(mainViewModel.getGarbageCategoryModalClickWordEng(), mainViewModel.getAreaModalClickWordEng(), 4, mainViewModel.getActivityStatus()) // 내 활동 조회
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        mainViewModel.clearErrorMsg()
        super.onDestroyView()
    }

    override fun onBackPressed() {
        activity.finish()
    }
}