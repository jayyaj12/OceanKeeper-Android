package com.letspl.oceankeeper.ui.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.letspl.oceankeeper.R
import com.letspl.oceankeeper.databinding.FragmentActivityRecruitBinding
import com.letspl.oceankeeper.ui.adapter.ActivityStartCalendarAdapter
import com.letspl.oceankeeper.ui.adapter.RecruitEndCalendarAdapter
import com.letspl.oceankeeper.ui.adapter.RecruitStartCalendarAdapter
import com.letspl.oceankeeper.ui.viewmodel.ActivityRecruitViewModel
import com.letspl.oceankeeper.util.EntryPoint
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
        activityRecruitViewModel.clearTempData()
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
        loadTempData()
    }

    // 주소 불러오기
    private fun loadAddress() {
        // 활동 위치
        binding.activityLocationEt.setText(activityRecruitViewModel.getLocationInfo().address)
    }

    // 캘린더 date 셋업
    private fun setupCalendarDate() {
        Timber.e("activityRecruitViewModel.isLoadTempData() ${activityRecruitViewModel.isLoadTempData()}")
        if(activityRecruitViewModel.isLoadTempData() == "") {
            activityRecruitViewModel.getNowDate()
        } else {
            activityRecruitViewModel.getNowDate(true)
        }
    }

    // 클릭 리스너
    private fun setUpClickedListener() {
        binding.activityLocationV.setOnClickListener {
            activityRecruitViewModel.setIsLoadTempData("address")
            EntryPoint.searchMapPoint = "recruit"
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
            if(it.toString() != "") {
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
            activityRecruitViewModel.setActivityStartTimeMinute(newValue)
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

    // 임시저장 데이터 불러오기
    private fun loadTempData() {
        Timber.e("quota ${activityRecruitViewModel.getQuota()}")
        Timber.e("getRecruitStartDate ${activityRecruitViewModel.getRecruitStartDate()}")
        Timber.e("getRecruitEndDate ${activityRecruitViewModel.getRecruitEndDate()}")
        Timber.e("getActivityStartDate ${activityRecruitViewModel.getActivityStartDate()}")
        Timber.e("activityRecruitViewModel.isLoadTempData() ${activityRecruitViewModel.isLoadTempData()}")
        if(activityRecruitViewModel.isLoadTempData() != "") {
            if(activityRecruitViewModel.isLoadTempData() == "temp") {
                binding.recruitStartDateTv.setTextColor(Color.parseColor("#212121"))
                binding.recruitEndDateTv.setTextColor(Color.parseColor("#212121"))
                binding.activityStartDateTv.setTextColor(Color.parseColor("#212121"))
                binding.endDateTv.setTextColor(Color.parseColor("#212121"))
            }

            if(activityRecruitViewModel.getRecruitStartClickedDate() != "") {
                // 모집 시작일
                binding.recruitStartDateTv.setTextColor(Color.parseColor("#212121"))
                binding.recruitStartDateTv.text = activityRecruitViewModel.getRecruitStartDate().split("T")[0].replace("-", ".")
            }

            if(activityRecruitViewModel.getRecruitEndClickedDate() != "") {
                // 종료일
                binding.recruitEndDateTv.setTextColor(Color.parseColor("#212121"))
                binding.recruitEndDateTv.text = activityRecruitViewModel.getRecruitEndDate().split("T")[0].replace("-", ".")
            }

            if(activityRecruitViewModel.getActivityStartClickedDate() != "") {
                // 활동 시작일
                binding.activityStartDateTv.setTextColor(Color.parseColor("#212121"))
                binding.activityStartDateTv.text = activityRecruitViewModel.getActivityStartDate().split("T")[0].replace("-", ".")
                binding.endDateTv.setTextColor(Color.parseColor("#212121"))
                binding.endDateTv.text = activityRecruitViewModel.getActivityStartDate().substring(11, 16)
            }

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
            if(activityRecruitViewModel.getQuota() == 0) {
                binding.quotaEt.setText("")
            } else {
                binding.quotaEt.setText(activityRecruitViewModel.getQuota().toString())
            }
            // 준비물
            binding.materialEt.setText(activityRecruitViewModel.getMaterial())
            // 제공 리워드
            binding.giveRewardEt.setText(activityRecruitViewModel.getGiveRewardStr())
            // 기타 안내 사항
            binding.otherGuideEt.setText(activityRecruitViewModel.getOtherGuide())
            // 참여 키퍼 리워드 제공 여부
            activityRecruitViewModel.setIsGiveReward(activityRecruitViewModel.isGiveReward())
            binding.rewardSwtich.isChecked = activityRecruitViewModel.isGiveReward()

            recruitStartCalendarAdapter.notifyDataSetChanged()
            recruitEndCalendarAdapter.notifyDataSetChanged()
            activityStartCalendarAdapter.notifyDataSetChanged()
        }
    }

    // 이전 달 버튼 클릭
    fun onMovePreviousMonth(type: Int) {
        Timber.e("onMovePreviousMonth")
        activityRecruitViewModel.onPreviousBtnClicked(type)
    }

    // 다음 달 버튼 클릭
    fun onMoveNextMonth(type: Int) {
        Timber.e("onMoveNextMonth")
        activityRecruitViewModel.onNextBtnClicked(type)
    }

    // 다음 버튼 클릭
    fun onClickNextBtn() {
        Timber.e("getRecruitStartDate ${activityRecruitViewModel.getRecruitStartDate()}")
        Timber.e("getRecruitEndDate ${activityRecruitViewModel.getRecruitEndDate()}")
        Timber.e("getActivityStartDate ${activityRecruitViewModel.getActivityStartDate()}")

        if(activityRecruitViewModel.isExistNeedData()) {
            // 임시저장 활성화
            when(val msg = activityRecruitViewModel.isQuotaCorrect()) {
                "성공" -> {
                    activityRecruitViewModel.setIsLoadTempData("temp")
                    activity.onReplaceFragment(ActivityRecruit2Fragment())
                }
                else -> activity.showErrorMsg(msg)
            }
        } else {
            activity.showErrorMsg("모든 값을 입력해주세요.")
        }
    }

    // 이전 버튼 클릭
    fun  onClickBackBtn() {
        // 활동 모집 데이터 초기화
        activityRecruitViewModel.clearTempData()

        // 임시저장 활성화
        activityRecruitViewModel.setIsLoadTempData("temp")
        activity.onReplaceFragment(MainFragment(), false, true)
    }

    // 임시저장 버튼 클릭
    fun onClickTempSaveBtn() {
        // 임시저장 활성화
        activityRecruitViewModel.setIsLoadTempData("temp")
        activity.onReplaceFragment(MainFragment(), false, true)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}