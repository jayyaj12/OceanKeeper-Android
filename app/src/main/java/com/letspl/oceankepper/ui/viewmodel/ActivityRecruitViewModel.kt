package com.letspl.oceankepper.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letspl.oceankepper.data.model.ActivityRecruitModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ActivityRecruitViewModel: ViewModel() {
    // 날짜 텍스트
    private var _choiceDateText = MutableLiveData<String>()
    val choiceDateText:LiveData<String> get() = _choiceDateText
    // 프로젝트명 길이
    private var _projectNameLength = MutableLiveData<Int>()
    val projectNameLength:LiveData<Int> get() = _projectNameLength
    // 교통 안내 가이드
    private var _trafficGuideValue = MutableLiveData<Int>()
    val trafficGuideValue:LiveData<Int> get() = _trafficGuideValue
    // 모집 카테고리 값
    private var _recruitCategory = MutableLiveData<Int>()
    val recruitCategory:LiveData<Int> get() = _recruitCategory
    // 지도 검색 결과값
    private var _searchMap = MutableLiveData<String>()
    val searchMap:LiveData<String> get() = _searchMap

    init {
        getNowDate()
        getDayInMonthArray()
    }

    // 이전 월 버튼 클릭 시
    fun onPreviousBtnClicked() {
        if(ActivityRecruitModel.nowMonth - 1 == 0) {
            ActivityRecruitModel.nowYear = ActivityRecruitModel.nowYear - 1
            ActivityRecruitModel.nowMonth = 12
        } else {
            ActivityRecruitModel.nowMonth = ActivityRecruitModel.nowMonth - 1
        }

        setDateClickPosition(-1)

        _choiceDateText.value = "${getMonthStr()} ${ActivityRecruitModel.nowYear}"
    }

    // 다음 월 버튼 클릭 시
    fun onNextBtnClicked() {
        if(ActivityRecruitModel.nowMonth + 1 == 13) {
            ActivityRecruitModel.nowYear = ActivityRecruitModel.nowYear + 1
            ActivityRecruitModel.nowMonth = 1
        } else {
            ActivityRecruitModel.nowMonth = ActivityRecruitModel.nowMonth + 1
        }

        setDateClickPosition(-1)

        _choiceDateText.value = "${getMonthStr()} ${ActivityRecruitModel.nowYear}"
    }

    // 현재 날짜 가져오기
    private fun getNowDate() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val nowDate = Date(System.currentTimeMillis())
        val dateStr = dateFormat.format(nowDate)

        ActivityRecruitModel.nowYear = dateStr.split("-")[0].toInt()
        ActivityRecruitModel.nowMonth = dateStr.split("-")[1].toInt()
        ActivityRecruitModel.nowDate  = dateStr.split("-")[2].toInt()

        _choiceDateText.value = "${getMonthStr()} ${ActivityRecruitModel.nowYear}"
    }

    private fun getMonthStr(): String {
        return when(ActivityRecruitModel.nowMonth) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> ""
        }
    }

    fun getDayInMonthArray(): ArrayList<String> {
        val dayList = arrayListOf<String>()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, ActivityRecruitModel.nowYear)
        calendar.set(Calendar.MONTH, ActivityRecruitModel.nowMonth - 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        Timber.e("year ${ActivityRecruitModel.nowYear}")
        Timber.e("month ${ActivityRecruitModel.nowMonth}")

        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        for(i in 2 until 42) {
            if(i <= dayOfWeek || i > lastDay + dayOfWeek) {
                dayList.add("")
            } else {
                dayList.add("${i - dayOfWeek}")
            }
        }

        return dayList
    }

    fun getDayOfWeek(date: Int): String {
        return when(date){
            1 -> "일"
            2 -> "월"
            3 -> "화"
            4 -> "수"
            5 -> "목"
            6 -> "금"
            7 -> "토"
            else -> ""
        }
    }

    // 클릭된 날짜 저장
    fun setDateClickPosition(pos: Int) {
        ActivityRecruitModel.clickedPosition = pos
    }

    // 클릭된 날짜 불러오기
    fun getDateClickPosition(): Int {
        return ActivityRecruitModel.clickedPosition
    }

    // 프로젝트명 길이 텍스트 변경
    fun onChangedProjectNameEditText(str: String) {
        _projectNameLength.postValue(str.length)
    }

    /**
     * 1: 카셰어링 연결 예정
     * 2: 단체차량 대절 예정
     * 3: 교통편 제공 없음
     */
    // 교통 안내 여부 값 변경
    fun setTrafficGuideValue(value: Int) {
        _trafficGuideValue.postValue(value)
    }

    /**
     * 1: 연안쓰레기
     * 2: 부유쓰레기
     * 3: 침적쓰레기
     * 4: 기타
     */
    // 모집 카테고리 값 변경
    fun setRecruitCategoryValue(value: Int) {
        _recruitCategory.postValue(value)
    }

    // 지도 검색 결과값
    fun setSearchMapResult(value: String) {
        Timber.e("setSearchMapResult $value")
        _searchMap.postValue(value)
    }

}