package com.letspl.oceankepper.ui.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.FragmentActivityRecruitBinding
import com.letspl.oceankepper.ui.adapter.ActivityStartCalendarAdapter
import com.letspl.oceankepper.ui.adapter.RecruitEndCalendarAdapter
import com.letspl.oceankepper.ui.adapter.RecruitStartCalendarAdapter
import com.letspl.oceankepper.ui.viewmodel.ActivityRecruitViewModel
import timber.log.Timber

class ActivityRecruitFragment : Fragment(), BaseActivity.OnBackPressedListener {
    private var _binding: FragmentActivityRecruitBinding? = null
    private val binding: FragmentActivityRecruitBinding get() = _binding!!
    private lateinit var recruitStartCalendarAdapter: RecruitStartCalendarAdapter
    private lateinit var recruitEndCalendarAdapter: RecruitEndCalendarAdapter
    private lateinit var activityStartCalendarAdapter: ActivityStartCalendarAdapter
    private val activityRecruitViewModel: ActivityRecruitViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
    }

    override fun onBackPressed() {
        activity.onReplaceFragment(MainFragment(), false, true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActivityRecruitBinding.inflate(layoutInflater)
        binding.activityRecruitViewModel = activityRecruitViewModel
        binding.activityRecruitFragment = this
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpCalendarRecyclerView()
        setUpViewModelObserver()
        setupCalendarDate()
        setUpActivityTimeSpinner()
        setUpEditTextListener()
        setUpClickedListener()
        setupRewardSwitchListener()
        loadAddress()

        // 임시저장 활성화 시 data 를 load 한다.
        if(activityRecruitViewModel.isLoadTempData()) {
            loadTempData()
        }
    }

    // 주소 불러오기 
    private fun loadAddress() {
        // 활동 위치
        binding.activityLocationEt.setText(activityRecruitViewModel.getLocationInfo().address)
    }

    // 임시저장 데이터 불러오기
    private fun loadTempData() {
        // 프로젝트 명
        binding.projectNameEt.setText(activityRecruitViewModel.getProjectName())
        // 교통 안내 여부
        activityRecruitViewModel.setTrafficGuideValue(activityRecruitViewModel.getGuideTrafficValue())
        // 모집 카테고리 선택
        activityRecruitViewModel.setRecruitCategoryValue(activityRecruitViewModel.getRecruitCategoryValue())
        // 모집 지역 선택
        activityRecruitViewModel.setRecruitLocationValue(activityRecruitViewModel.getRecruitLocationValue())
        // 활동 프로그램 안내
        binding.guideActivityEt.setText(activityRecruitViewModel.getGuideActivity())
        // 모집 정원
        binding.quotaEt.setText(activityRecruitViewModel.getQuota().toString())
        // 준비물
        binding.materialEt.setText(activityRecruitViewModel.getMaterial())
        // 제공 리워드
        binding.giveRewardEt.setText(activityRecruitViewModel.getGiveRewardStr())
        // 기타 안내 사항
        binding.otherGuideEt.setText(activityRecruitViewModel.getOtherGuide())
        // 참여 키퍼 리워드 제공 여부
        activityRecruitViewModel.setIsGiveReward(activityRecruitViewModel.isGiveReward())
        Timber.e("// 참여 키퍼 리워드 제공 여부 스위치 ${activityRecruitViewModel.isGiveReward()}")
        binding.rewardSwtich.isChecked = activityRecruitViewModel.isGiveReward()

        recruitStartCalendarAdapter.notifyDataSetChanged()
        recruitEndCalendarAdapter.notifyDataSetChanged()
        activityStartCalendarAdapter.notifyDataSetChanged()
    }

    // 캘린더 date 셋업
    private fun setupCalendarDate() {
        activityRecruitViewModel.getNowDate()
    }

    // 클릭 리스너
    private fun setUpClickedListener() {
        binding.activityLocationV.setOnClickListener {
            activityRecruitViewModel.setIsLoadTempData(true)
            activity.onReplaceFragment(SearchMapFragment())
        }
    }

    // 제공 리워드 스위치 리스너
    private fun setupRewardSwitchListener() {
        binding.rewardSwtich.setOnCheckedChangeListener { buttonView, isChecked ->
            activityRecruitViewModel.setIsGiveReward(isChecked)
        }
    }

    private fun setUpViewModelObserver() {
        // 모집 시작일 날짜 선택
        activityRecruitViewModel.choiceRecruitStartDateText.observe(viewLifecycleOwner) {
            recruitStartCalendarAdapter.setDateArr(activityRecruitViewModel.getDayInMonthArray(1))
            recruitStartCalendarAdapter.notifyDataSetChanged()
        }
        // 모집 종료일 날짜 선택
        activityRecruitViewModel.choiceRecruitEndDateText.observe(viewLifecycleOwner) {
            recruitEndCalendarAdapter.setDateArr(activityRecruitViewModel.getDayInMonthArray(2))
            recruitEndCalendarAdapter.notifyDataSetChanged()
        }
        // 활동 시작일 날짜 선택
        activityRecruitViewModel.choiceActivityStartDateText.observe(viewLifecycleOwner) {
            activityStartCalendarAdapter.setDateArr(activityRecruitViewModel.getDayInMonthArray(3))
            activityStartCalendarAdapter.notifyDataSetChanged()
        }
    }

    // calendarRecyclerview 셋업
    private fun setUpCalendarRecyclerView() {
        recruitStartCalendarAdapter = RecruitStartCalendarAdapter(activityRecruitViewModel){
            binding.recruitStartDateTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_scale_g900))
            binding.recruitStartDateTv.text = activityRecruitViewModel.getRecruitStartDate().substring(0, 10).replace("-", ".")
        }
        recruitEndCalendarAdapter = RecruitEndCalendarAdapter(activityRecruitViewModel){
            binding.recruitEndDateTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_scale_g900))
            binding.recruitEndDateTv.text = activityRecruitViewModel.getRecruitEndDate().substring(0, 10).replace("-", ".")
        }
        activityStartCalendarAdapter = ActivityStartCalendarAdapter(activityRecruitViewModel){
            binding.activityStartDateTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_scale_g900))
            binding.activityStartDateTv.text = activityRecruitViewModel.getActivityStartDate().substring(0, 10).replace("-", ".")
        }

        recruitStartCalendarAdapter.setDateArr(activityRecruitViewModel.getDayInMonthArray(1))
        binding.calendarStartRecruitRv.run {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = recruitStartCalendarAdapter
        }

        recruitEndCalendarAdapter.setDateArr(activityRecruitViewModel.getDayInMonthArray(2))
        binding.calendarEndRecruitRv.run {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = recruitEndCalendarAdapter
        }

        activityStartCalendarAdapter.setDateArr(activityRecruitViewModel.getDayInMonthArray(3))
        binding.calendarActivityRv.run {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = activityStartCalendarAdapter
        }

        recruitStartCalendarAdapter.notifyDataSetChanged()
        recruitEndCalendarAdapter.notifyDataSetChanged()
        activityStartCalendarAdapter.notifyDataSetChanged()
    }

    // editText Listener 등록
    private fun setUpEditTextListener() {
        binding.quotaEt.addTextChangedListener {
            if(it.toString().isNotEmpty()) {
                activityRecruitViewModel.setQuota(it.toString().toInt())
            } else {
                activityRecruitViewModel.setQuota(0)
            }
        }
        binding.projectNameEt.addTextChangedListener {
            activityRecruitViewModel.onChangedProjectNameEditText(it.toString())
        }
        binding.guideActivityEt.addTextChangedListener {
            activityRecruitViewModel.setGuideActivity(it.toString())
        }
        binding.materialEt.addTextChangedListener {
            activityRecruitViewModel.setMaterial(it.toString())
        }
        binding.giveRewardEt.addTextChangedListener {
            activityRecruitViewModel.setGiveReward(it.toString())
        }
        binding.otherGuideEt.addTextChangedListener {
            activityRecruitViewModel.setOtherGuide(it.toString())
        }
    }

    // 날짜 선택 팝업 설정
    private fun setUpActivityTimeSpinner() {
        val hourPicker = binding.hourPicker
        val minutePicker = binding.minutePicker
        val morningAndAfternoonPicker = binding.morningAndAfternoonPicker

        // 최대값 세팅
        hourPicker.minValue = 0
        hourPicker.maxValue = 11
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        morningAndAfternoonPicker.minValue = 0
        morningAndAfternoonPicker.maxValue = 1

        val hourValues = ArrayList<String>()
        val minuteValues = ArrayList<String>()
        val morningAndAfternoonValue = ArrayList<String>()

        // 시간 세팅
        for (i in 1..12) {
            hourValues.add("$i")
        }

        hourPicker.displayedValues = hourValues.toTypedArray()

        // 분 세팅
        for (i in 0..59) {
            minuteValues.add("$i")
        }

        minutePicker.displayedValues = minuteValues.toTypedArray()

        // 오전 오후 세팅
        morningAndAfternoonValue.add("AM")
        morningAndAfternoonValue.add("PM")

        morningAndAfternoonPicker.displayedValues = morningAndAfternoonValue.toTypedArray()

        hourPicker.setOnValueChangedListener { numberPicker, oldValue, newValue ->
            activityRecruitViewModel.setActivityStartTimeHour(newValue + 1, morningAndAfternoonValue[morningAndAfternoonPicker.value])
            activityRecruitViewModel.setActivityStartTimeMinute(minuteValues[minutePicker.value].toInt())
            binding.endDateTv.text = activityRecruitViewModel.getActivityStartDate().substring(11, 16)
            binding.endDateTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_scale_g900))
        }

        minutePicker.setOnValueChangedListener { numberPicker, oldValue, newValue ->
            activityRecruitViewModel.setActivityStartTimeHour(hourValues[hourPicker.value].toInt(), morningAndAfternoonValue[morningAndAfternoonPicker.value])
            activityRecruitViewModel.setActivityStartTimeMinute(newValue + 1)
            binding.endDateTv.text = activityRecruitViewModel.getActivityStartDate().substring(11, 16)
            binding.endDateTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_scale_g900))
        }

        morningAndAfternoonPicker.setOnValueChangedListener { numberPicker, i, newValue ->
            activityRecruitViewModel.setActivityStartTimeHour(hourValues[hourPicker.value].toInt(), morningAndAfternoonValue[morningAndAfternoonPicker.value])
            activityRecruitViewModel.setActivityStartTimeMinute(minuteValues[minutePicker.value].toInt())
            binding.endDateTv.text = activityRecruitViewModel.getActivityStartDate().substring(11, 16)
            binding.endDateTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_scale_g900))
        }
    }

    // 이전 달 버튼 클릭
    fun onMovePreviousMonth(type: Int) {
        activityRecruitViewModel.onPreviousBtnClicked(type)
    }

    // 다음 달 버튼 클릭
    fun onMoveNextMonth(type: Int) {
        activityRecruitViewModel.onNextBtnClicked(type)
    }

    // 다음 버튼 클릭
    fun onClickNextBtn() {
        if(activityRecruitViewModel.isExistNeedData()) {
            // 임시저장 활성화
            activityRecruitViewModel.setIsLoadTempData(true)
            activity.onReplaceFragment(ActivityRecruit2Fragment())
        } else {
            Toast.makeText(requireContext(), "모든 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 이전 버튼 클릭
    fun  onClickBackBtn() {
        // 활동 모집 데이터 초기화
        activityRecruitViewModel.clearTempData()

        // 임시저장 활성화
        activityRecruitViewModel.setIsLoadTempData(false)
        activity.onReplaceFragment(MainFragment(), false, true)
    }

    // 임시저장 버튼 클릭
    fun onClickTempSaveBtn() {
        // 임시저장 활성화
        activityRecruitViewModel.setIsLoadTempData(true)
        activity.onReplaceFragment(MainFragment(), false, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}