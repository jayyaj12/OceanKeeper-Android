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
        setUpClickedListener()
    }

    private fun setUpClickedListener() {
        binding.activityLocationEt.setOnClickListener {
            activity.onReplaceFragment(SearchMapFragment())
        }
    }

    private fun setUpViewModels() {
        // 날짜 선택
        activityRecruitViewModel.choiceRecruitStartDateText.observe(viewLifecycleOwner) {
            calendarAdapter.setDateArr(activityRecruitViewModel.getDayInMonthArray())
            calendarAdapter.notifyDataSetChanged()
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
        binding.calendarStartRecruitRv.run {
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
    fun onMovePreviousMonth(type: Int) {
        activityRecruitViewModel.onPreviousBtnClicked(type)
    }

    // 다음 달 버튼 클릭
    fun onMoveNextMonth(type: Int) {
        activityRecruitViewModel.onNextBtnClicked(type)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}