package com.letspl.oceankepper.ui.view

import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.letspl.oceankepper.R
import com.letspl.oceankepper.databinding.FragmentActivityRecruitBinding
import com.letspl.oceankepper.ui.adapter.CalendarAdapter
import com.letspl.oceankepper.ui.viewmodel.ActivityRecruitViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.text.SimpleDateFormat
import javax.inject.Inject

class ActivityRecruitFragment : Fragment() {
    private var _binding: FragmentActivityRecruitBinding? = null
    private val binding: FragmentActivityRecruitBinding get() = _binding!!
    private lateinit var calendarAdapter: CalendarAdapter
    private val activityRecruitViewModel: ActivityRecruitViewModel by viewModels()
    private val activity: BaseActivity by lazy {
        requireActivity() as BaseActivity
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
        setUpViewModels()
        setUpActivityTimeSpinner()
        setUpEditTextListener()
        setUpTrafficGuideCheckBox()
        setUpRecruitCategory()
        setUpClickedListener()
    }

    private fun setUpClickedListener() {
        binding.activityLocationEt.setOnClickListener {
            activity.onReplaceFragment(SearchMapFragment())
        }
    }

    private fun setUpViewModels() {
        // 날짜 선택
        activityRecruitViewModel.choiceDateText.observe(viewLifecycleOwner) {
            calendarAdapter.setDateArr(activityRecruitViewModel.getDayInMonthArray())
            calendarAdapter.notifyDataSetChanged()
        }

        // 프로젝트 명 길이
        activityRecruitViewModel.projectNameLength.observe(viewLifecycleOwner) {
            binding.projectNameLengthTv.text = "글자수 제한 ${it}/30"
        }

        // 교통 정보 선택
        activityRecruitViewModel.trafficGuideValue.observe(viewLifecycleOwner) {
            when (it) {
                1 -> {
                    binding.carSharingV.setBackgroundResource(R.drawable.traffic_guide_checked)
                    binding.groupCarSharingV.setBackgroundResource(R.drawable.radius_6_solid_fff_stroke_g300)
                    binding.noCarSharingV.setBackgroundResource(R.drawable.radius_6_solid_fff_stroke_g300)
                }
                2 -> {
                    binding.carSharingV.setBackgroundResource(R.drawable.radius_6_solid_fff_stroke_g300)
                    binding.groupCarSharingV.setBackgroundResource(R.drawable.traffic_guide_checked)
                    binding.noCarSharingV.setBackgroundResource(R.drawable.radius_6_solid_fff_stroke_g300)
                }
                3 -> {
                    binding.carSharingV.setBackgroundResource(R.drawable.radius_6_solid_fff_stroke_g300)
                    binding.groupCarSharingV.setBackgroundResource(R.drawable.radius_6_solid_fff_stroke_g300)
                    binding.noCarSharingV.setBackgroundResource(R.drawable.traffic_guide_checked)
                }
            }
        }

        // 모집 카테고리 선택
        activityRecruitViewModel.recruitCategory.observe(viewLifecycleOwner) {
            when (it) {
                1 -> {
                    onChangeFontColorAndBackground(binding.category1Tv, true)
                    onChangeFontColorAndBackground(binding.category2Tv, false)
                    onChangeFontColorAndBackground(binding.category3Tv, false)
                    onChangeFontColorAndBackground(binding.category4Tv, false)
                }
                2 -> {
                    onChangeFontColorAndBackground(binding.category1Tv, false)
                    onChangeFontColorAndBackground(binding.category2Tv, true)
                    onChangeFontColorAndBackground(binding.category3Tv, false)
                    onChangeFontColorAndBackground(binding.category4Tv, false)
                }
                3 -> {
                    onChangeFontColorAndBackground(binding.category1Tv, false)
                    onChangeFontColorAndBackground(binding.category2Tv, false)
                    onChangeFontColorAndBackground(binding.category3Tv, true)
                    onChangeFontColorAndBackground(binding.category4Tv, false)
                }
                4 -> {
                    onChangeFontColorAndBackground(binding.category1Tv, false)
                    onChangeFontColorAndBackground(binding.category2Tv, false)
                    onChangeFontColorAndBackground(binding.category3Tv, false)
                    onChangeFontColorAndBackground(binding.category4Tv, true)
                }
            }
        }
    }

    // 텍스트 색상, 배경 변경
    private fun onChangeFontColorAndBackground(textView: TextView, checked: Boolean) {
        if (checked) {
            textView.setTextColor(Color.parseColor("#079299"))
            textView.setBackgroundResource(R.drawable.radius_20_solid_p50)
        } else {
            textView.setTextColor(Color.parseColor("#B0BEC5"))
            textView.setBackgroundResource(R.drawable.radius_20_solid_bg50)
        }
    }

    // calendarRecyclerview 셋업
    private fun setUpCalendarRecyclerView() {
        calendarAdapter = CalendarAdapter(activityRecruitViewModel)

        calendarAdapter.setDateArr(activityRecruitViewModel.getDayInMonthArray())
        binding.calendarRv.run {
            layoutManager = GridLayoutManager(requireContext(), 7)
            adapter = calendarAdapter
        }

        calendarAdapter.notifyDataSetChanged()
    }

    // editText Listener 등록
    private fun setUpEditTextListener() {
        binding.projectNameEt.addTextChangedListener {
            activityRecruitViewModel.onChangedProjectNameEditText(it.toString())
        }
    }

    // 교통 안내 체크박스
    private fun setUpTrafficGuideCheckBox() {
        binding.carSharingV.setOnClickListener {
            activityRecruitViewModel.setTrafficGuideValue(1)
        }
        binding.groupCarSharingV.setOnClickListener {
            activityRecruitViewModel.setTrafficGuideValue(2)
        }
        binding.noCarSharingV.setOnClickListener {
            activityRecruitViewModel.setTrafficGuideValue(3)
        }
    }

    // 모집 카테고리 선택
    private fun setUpRecruitCategory() {
        binding.category1Tv.setOnClickListener {
            activityRecruitViewModel.setRecruitCategoryValue(1)
        }
        binding.category2Tv.setOnClickListener {
            activityRecruitViewModel.setRecruitCategoryValue(2)
        }
        binding.category3Tv.setOnClickListener {
            activityRecruitViewModel.setRecruitCategoryValue(3)
        }
        binding.category4Tv.setOnClickListener {
            activityRecruitViewModel.setRecruitCategoryValue(4)
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
        minutePicker.maxValue = 60
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
        for (i in 0..60) {
            minuteValues.add("$i")
        }

        minutePicker.displayedValues = minuteValues.toTypedArray()

        // 오전 오후 세팅
        morningAndAfternoonValue.add("AM")
        morningAndAfternoonValue.add("PM")

        morningAndAfternoonPicker.displayedValues = morningAndAfternoonValue.toTypedArray()
    }

    // 이전 달 버튼 클릭
    fun onMovePreviousMonth() {
        activityRecruitViewModel.onPreviousBtnClicked()
    }

    // 다음 달 버튼 클릭
    fun onMoveNextMonth() {
        activityRecruitViewModel.onNextBtnClicked()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}