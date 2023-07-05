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

class ActivityRecruitViewModel : ViewModel() {
    // 날짜 텍스트
    private var _choiceRecruitStartDateText = MutableLiveData<String>()
    val choiceRecruitStartDateText: LiveData<String> get() = _choiceRecruitStartDateText

    // 날짜 텍스트
    private var _choiceRecruitEndDateText = MutableLiveData<String>()
    val choiceRecruitEndDateText: LiveData<String> get() = _choiceRecruitEndDateText

    // 날짜 텍스트
    private var _choiceActivityStartDateText = MutableLiveData<String>()
    val choiceActivityStartDateText: LiveData<String> get() = _choiceActivityStartDateText

    // 프로젝트명 길이
    private var _projectNameLength = MutableLiveData<Int>(0)
    val projectNameLength: LiveData<Int> get() = _projectNameLength

    // 교통 안내 가이드
    private var _trafficGuideValue = MutableLiveData<Int>(0)
    val trafficGuideValue: LiveData<Int> get() = _trafficGuideValue

    // 모집 카테고리 값
    private var _recruitCategory = MutableLiveData<Int>(1)
    val recruitCategory: LiveData<Int> get() = _recruitCategory

    // 모집 지역 값
    private var _recruitLocation = MutableLiveData<Int>(1)
    val recruitLocation: LiveData<Int> get() = _recruitLocation

    // 지도 검색 결과값
    private var _searchMap = MutableLiveData<String>()
    val searchMap: LiveData<String> get() = _searchMap

    // 모집 시작일, 종료일 컴포넌트 선택값
    private var _clickedRecruitComponent = MutableLiveData<Int>(0)
    val clickedRecruitComponent: LiveData<Int> get() = _clickedRecruitComponent

    // 활동 시작일, 종료일 컴포넌트 선택값
    private var _clickedActivityComponent = MutableLiveData<Int>(0)
    val clickedActivityComponent: LiveData<Int> get() = _clickedActivityComponent

    init {
        getNowDate()
        getDayInMonthArray()
    }

    // 이전 월 버튼 클릭 시
    /**
     * 1: 모집 시작일 캘린더 달력
     * 2: 모집 종료일 캘린더 달력
     * 3: 활동 시작일 캘린더 달력
     */
    fun onPreviousBtnClicked(type: Int) {
        when(type) {
            1 -> onMinusChoiceDateValue(type)
            2 -> onMinusChoiceDateValue(type)
            3 -> onMinusChoiceDateValue(type)
        }
    }

    // 다음 월 버튼 클릭 시
    /**
     * 1: 모집 시작일 캘린더 달력
     * 2: 모집 종료일 캘린더 달력
     * 3: 활동 시작일 캘린더 달력
     */
    fun onNextBtnClicked(type: Int) {
        when(type) {
            1 -> onPlusChoiceDateValue(type)
            2 -> onPlusChoiceDateValue(type)
            3 -> onPlusChoiceDateValue(type)
        }
    }

    private fun onPlusChoiceDateValue(type: Int) {
        when (type) {
            1 -> {
                if (ActivityRecruitModel.recruitStartNowMonth + 1 == 13) {
                    ActivityRecruitModel.recruitStartNowYear = ActivityRecruitModel.recruitStartNowYear + 1
                    ActivityRecruitModel.recruitStartNowMonth = 1
                } else {
                    ActivityRecruitModel.recruitStartNowMonth = ActivityRecruitModel.recruitStartNowMonth + 1
                }

                setRecruitStartDateClickPosition(-1)

                _choiceRecruitStartDateText.value = "${getMonthStr(1)} ${ActivityRecruitModel.recruitStartNowYear}"
            }
            2 -> {
                if (ActivityRecruitModel.recruitEndNowMonth + 1 == 13) {
                    ActivityRecruitModel.recruitEndNowYear = ActivityRecruitModel.recruitEndNowYear + 1
                    ActivityRecruitModel.recruitEndNowMonth = 1
                } else {
                    ActivityRecruitModel.recruitEndNowMonth = ActivityRecruitModel.recruitEndNowMonth + 1
                }

                setRecruitEndDateClickPosition(-1)

                _choiceRecruitEndDateText.value = "${getMonthStr(2)} ${ActivityRecruitModel.recruitEndNowYear}"
            }
            3 -> {
                if (ActivityRecruitModel.activityStartNowMonth + 1 == 13) {
                    ActivityRecruitModel.activityStartNowYear = ActivityRecruitModel.activityStartNowYear + 1
                    ActivityRecruitModel.activityStartNowMonth = 1
                } else {
                    ActivityRecruitModel.activityStartNowMonth = ActivityRecruitModel.activityStartNowMonth + 1
                }

                setActivityStartDateClickPosition(-1)

                _choiceActivityStartDateText.value = "${getMonthStr(3)} ${ActivityRecruitModel.activityStartNowYear}"
            }
        }
    }

    private fun onMinusChoiceDateValue(type: Int) {
        when (type) {
            1 -> {
                if (ActivityRecruitModel.recruitStartNowMonth - 1 == 0) {
                    ActivityRecruitModel.recruitStartNowYear = ActivityRecruitModel.recruitStartNowYear - 1
                    ActivityRecruitModel.recruitStartNowMonth = 12
                } else {
                    ActivityRecruitModel.recruitStartNowMonth =
                        ActivityRecruitModel.recruitStartNowMonth - 1
                }

                setRecruitStartDateClickPosition(-1)

                _choiceRecruitStartDateText.value =
                    "${getMonthStr(1)} ${ActivityRecruitModel.recruitStartNowYear}"
            }
            2 -> {
                if (ActivityRecruitModel.recruitEndNowMonth - 1 == 0) {
                    ActivityRecruitModel.recruitEndNowYear = ActivityRecruitModel.recruitEndNowYear - 1
                    ActivityRecruitModel.recruitEndNowMonth = 12
                } else {
                    ActivityRecruitModel.recruitEndNowMonth =
                        ActivityRecruitModel.recruitEndNowMonth - 1
                }

                setRecruitEndDateClickPosition(-1)

                _choiceRecruitEndDateText.value =
                    "${getMonthStr(2)} ${ActivityRecruitModel.recruitEndNowYear}"
            }
            3 -> {
                if (ActivityRecruitModel.activityStartNowMonth - 1 == 0) {
                    ActivityRecruitModel.activityStartNowYear = ActivityRecruitModel.activityStartNowYear - 1
                    ActivityRecruitModel.activityStartNowMonth = 12
                } else {
                    ActivityRecruitModel.activityStartNowMonth =
                        ActivityRecruitModel.activityStartNowMonth - 1
                }

                setActivityStartDateClickPosition(-1)

                _choiceActivityStartDateText.value =
                    "${getMonthStr(3)} ${ActivityRecruitModel.activityStartNowYear}"
            }
        }
    }

    // 현재 날짜 가져오기
    private fun getNowDate() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val nowDate = Date(System.currentTimeMillis())
        val dateStr = dateFormat.format(nowDate)

        ActivityRecruitModel.recruitStartNowYear = dateStr.split("-")[0].toInt()
        ActivityRecruitModel.recruitStartNowMonth = dateStr.split("-")[1].toInt()
        ActivityRecruitModel.recruitStartNowDate = dateStr.split("-")[2].toInt()
        ActivityRecruitModel.recruitEndNowYear = dateStr.split("-")[0].toInt()
        ActivityRecruitModel.recruitEndNowMonth = dateStr.split("-")[1].toInt()
        ActivityRecruitModel.recruitEndNowDate = dateStr.split("-")[2].toInt()
        ActivityRecruitModel.activityStartNowYear = dateStr.split("-")[0].toInt()
        ActivityRecruitModel.activityStartNowMonth = dateStr.split("-")[1].toInt()
        ActivityRecruitModel.activityStartNowDate = dateStr.split("-")[2].toInt()

        _choiceRecruitStartDateText.value =
            "${getMonthStr(1)} ${ActivityRecruitModel.recruitStartNowYear}"
        _choiceRecruitEndDateText.value =
            "${getMonthStr(1)} ${ActivityRecruitModel.recruitStartNowYear}"
        _choiceActivityStartDateText.value =
            "${getMonthStr(1)} ${ActivityRecruitModel.recruitStartNowYear}"
    }

    private fun getMonthStr(type: Int): String {
        return when (type) {
            1 -> {
                return when (ActivityRecruitModel.recruitStartNowMonth) {
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
            2 -> {
                return when (ActivityRecruitModel.recruitEndNowMonth) {
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
            else -> {
                return when (ActivityRecruitModel.activityStartNowMonth) {
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
        }
    }

    fun getDayInMonthArray(): ArrayList<String> {
        val dayList = arrayListOf<String>()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, ActivityRecruitModel.recruitStartNowYear)
        calendar.set(Calendar.MONTH, ActivityRecruitModel.recruitStartNowMonth - 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        Timber.e("year ${ActivityRecruitModel.recruitStartNowYear}")
        Timber.e("month ${ActivityRecruitModel.recruitStartNowMonth}")

        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        for (i in 2 until 42) {
            if (i <= dayOfWeek || i > lastDay + dayOfWeek) {
                dayList.add("")
            } else {
                dayList.add("${i - dayOfWeek}")
            }
        }

        return dayList
    }

    fun getDayOfWeek(date: Int): String {
        return when (date) {
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
    fun setRecruitStartDateClickPosition(pos: Int) {
        ActivityRecruitModel.recruitStartClickedPosition = pos
    }

    // 클릭된 날짜 불러오기
    fun getRecruitStartDateClickPosition(): Int {
        return ActivityRecruitModel.recruitStartClickedPosition
    }


    // 클릭된 날짜 저장
    fun setRecruitEndDateClickPosition(pos: Int) {
        ActivityRecruitModel.recruitEndClickedPosition = pos
    }

    // 클릭된 날짜 불러오기
    fun getRecruitEndDateClickPosition(): Int {
        return ActivityRecruitModel.recruitEndClickedPosition
    }


    // 클릭된 날짜 저장
    fun setActivityStartDateClickPosition(pos: Int) {
        ActivityRecruitModel.activityStartClickedPosition = pos
    }

    // 클릭된 날짜 불러오기
    fun getActivityStartDateClickPosition(): Int {
        return ActivityRecruitModel.activityStartClickedPosition
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

    /**
     * 1: 서해번쩍
     * 2: 동해번쩍
     * 3: 남해번쩍
     * 4: 제주번쩍
     * 5: 기타
     */
    // 모집 카테고리 값 변경
    fun setRecruitLocationValue(value: Int) {
        _recruitLocation.postValue(value)
    }

    // 지도 검색 결과값
    fun setSearchMapResult(value: String) {
        _searchMap.postValue(value)
    }

    // 모집 시작일, 종료일 컵포넌트 선택값
    fun setClickedRecruitComponent(value: Int) {
        _clickedRecruitComponent.postValue(value)
    }

    // 활동 시작일, 종료일 컵포넌트 선택값
    fun setClickedActivityComponent(value: Int) {
        _clickedActivityComponent.postValue(value)
    }

}