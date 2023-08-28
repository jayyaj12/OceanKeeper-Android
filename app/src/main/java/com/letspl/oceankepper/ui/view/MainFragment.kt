package com.letspl.oceankepper.ui.view

import MarginItemDecoration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.letspl.oceankepper.data.dto.ComingScheduleItem
import com.letspl.oceankepper.databinding.FragmentMainBinding
import com.letspl.oceankepper.ui.adapter.MainActivityListAdapter
import com.letspl.oceankepper.ui.adapter.MainComingScheduleAdapter
import com.letspl.oceankepper.ui.dialog.AreaChoiceDialog
import com.letspl.oceankepper.ui.dialog.GarbageCategoryChoiceDialog
import com.letspl.oceankepper.ui.viewmodel.MainViewModel
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
    private lateinit var adapter: MainActivityListAdapter

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

        setUpViewModelObservers()
        setupFixScrollView()
        setupRecyclerview()
        loadData()
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
        // 다가오는 일정 조회 완료되면 viewpager 자동 슬라이드 setup
        mainViewModel.getComingScheduleResult.observe(viewLifecycleOwner) {
            setupViewPager2(it)
        }

        // 내 활동 조회 결과 표시
        mainViewModel.getMyActivityResult.observe(viewLifecycleOwner) {
            Timber.e("getMyActivityResult $it")
            adapter.submitList(it.toMutableList())
        }

        // 활동 진행 상태 변경 시 활동을 재조회 한다.
        mainViewModel.activityStatusPosition.observe(viewLifecycleOwner) {
            mainViewModel.clearActivityList()
            mainViewModel.setLastActivity(false)
            mainViewModel.initGarbageLocationSelected()
            mainViewModel.getMyActivities(mainViewModel.getGarbageCategoryModalClickWordEng(), mainViewModel.getAreaModalClickWordEng(), 4, mainViewModel.getActivityStatus()) // 내 활동 조회
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
//        mainViewModel.getMyActivities(mainViewModel.getGarbageCategoryModalClickWordEng(), mainViewModel.getAreaModalClickWordEng(), 4, mainViewModel.getActivityStatus()) // 내 활동 조회
//        Timber.e("loadData")
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
        binding.scheduleViewPager.apply {
            clipToPadding = false
            clipChildren = false
            adapter = MainComingScheduleAdapter(list)
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

    // recyclerview 세팅
    private fun setupRecyclerview() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.activityRv.layoutManager = gridLayoutManager

        // 선택될 경우 activityId 값을 저장 후 상세 페이지로 이동
        adapter = MainActivityListAdapter(requireContext()) {
            mainViewModel.setClickedActivityId(it)
            activity.onReplaceFragment(ActivityDetailFragment(), false, false)
            mainViewModel.clearActivityList()
        }

        binding.activityRv.adapter = adapter

        binding.mainScrollview.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if(!binding.mainScrollview.canScrollVertically(1)) {
                Timber.e("최하단")
                mainViewModel.getMyActivities(mainViewModel.getGarbageCategoryModalClickWordEng(), mainViewModel.getAreaModalClickWordEng(), 4, mainViewModel.getActivityStatus()) // 내 활동 조회
            }

        }
        (object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Timber.e("onScrolled")


                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount - 1
                if (lastVisibleItemPosition == itemTotalCount) {
//                    Timber.e("최하단")
                }
            }
        })
//        binding.mainScrollview.viewTreeObserver.addOnScrollChangedListener {
//            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
//            val totalItemCount = layoutManager.itemCount
//
//            if (lastVisibleItemPosition == totalItemCount - 1) {
//                // 마지막 아이템이 화면에 보이는 경우 처리할 내용
//                // 예: 추가 데이터를 로드하거나 다른 작업 수행
//            }
//            if(_binding != null) {
//                if(isScrollViewAtBottom(binding.mainScrollview)){
//                    Timber.e("isScrollViewAtBottom")
//                    mainViewModel.getMyActivities(mainViewModel.getGarbageCategoryModalClickWordEng(), mainViewModel.getAreaModalClickWordEng(), 4, mainViewModel.getActivityStatus()) // 내 활동 조회
//                }
//            }
//        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onBackPressed() {
        activity.finish()
    }
}