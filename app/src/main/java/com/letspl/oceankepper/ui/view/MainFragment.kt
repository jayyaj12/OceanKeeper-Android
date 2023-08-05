package com.letspl.oceankepper.ui.view

import MarginItemDecoration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.letspl.oceankepper.data.dto.ActivityInfo
import com.letspl.oceankepper.data.dto.ComingScheduleItem
import com.letspl.oceankepper.databinding.FragmentMainBinding
import com.letspl.oceankepper.ui.adapter.MainActivityListAdapter
import com.letspl.oceankepper.ui.adapter.MainComingScheduleAdapter
import com.letspl.oceankepper.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainFragment: Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentMainBinding.inflate(layoutInflater)
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
        setupViewPager2()
        setupRecyclerview()
        loadData()
    }

    // viewModel 옵저버 세팅
    private fun setUpViewModelObservers() {
    }

    // 데이터 불러오기
    private fun loadData() {
        mainViewModel.selectComingSchedule()
    }

    // 자동 슬라이드 구현(viewpager2)
    private fun setupViewPager2() {
        val list = arrayListOf<ComingScheduleItem>()
        list.add(ComingScheduleItem(20, "","금능해변 플로깅 프로젝트", "03.20(월) 13시 시작", "제주도 능금해변"))
        list.add(ComingScheduleItem(20, "","금능해변 플로깅 프로젝트", "03.20(월) 13시 시작", "제주도 능금해변"))
        list.add(ComingScheduleItem(20,"", "금능해변 플로깅 프로젝트", "03.20(월) 13시 시작", "제주도 능금해변"))
        list.add(ComingScheduleItem(20,"", "금능해변 플로깅 프로젝트", "03.20(월) 13시 시작", "제주도 능금해변"))
        list.add(ComingScheduleItem(20,"", "금능해변 플로깅 프로젝트", "03.20(월) 13시 시작", "제주도 능금해변"))

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
                }
            })
        }
    }

    private fun mainViewChangeEvent(){
//        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
//            override fun onPageSelected(position: Int) {
//                content_text.text = textList[position]
//            }
//        })
    }

    fun isRecyclerViewAtBottom(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager as GridLayoutManager ?: return false
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        val totalItemCount = layoutManager.itemCount

        // 여기에서 원하는 조건을 추가하여 맨 아래로 스크롤되었는지 확인할 수 있습니다.
        // 예를 들어, 스크롤의 맨 아래로 가려면 다음과 같은 조건을 사용할 수 있습니다.
        return lastVisibleItemPosition == totalItemCount - 1
    }

    // recyclerview 세팅
    private fun setupRecyclerview() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.activityRv.layoutManager = gridLayoutManager

        val adapter = MainActivityListAdapter(requireContext())
        binding.activityRv.adapter = adapter

        val list = mutableListOf<ActivityInfo>()

        list.add(ActivityInfo("1", "1", "1", "1", 1, 1, "1", "1"))
        list.add(ActivityInfo("2", "2", "2", "2", 2, 2, "2", "2"))
        list.add(ActivityInfo("3", "3", "3", "3", 3, 3, "3", "3"))
        list.add(ActivityInfo("4", "4", "4", "4", 4, 4, "4", "4"))

        adapter.submitList(list)

        binding.activityRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                Timber.e("asdasd")

                if(binding.activityRv.canScrollVertically(1)) {
                    Timber.e("하단")
                } else if(binding.activityRv.canScrollVertically(-1)){
                    Timber.e("상단")
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                Timber.e("asdasd")

                if(recyclerView.canScrollVertically(1)) {
                    Timber.e("하단")
                } else if(recyclerView.canScrollVertically(-1)){
                    Timber.e("상단")
                }
            }
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onBackPressed() {
        activity.finish()
    }
}