package com.letspl.oceankepper.ui.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letspl.oceankepper.data.dto.ActivityRegisterLocationDto
import com.letspl.oceankepper.data.model.ActivityRecruitModel
import com.letspl.oceankepper.util.ContextUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class ActivityRecruitViewModel: ViewModel() {
    // 모집 시작일 날짜 텍스트
    private var _choiceRecruitStartDateText = MutableLiveData<String>("")
    val choiceRecruitStartDateText: LiveData<String> get() = _choiceRecruitStartDateText

    // 모집 종료일 날짜 텍스트
    private var _choiceRecruitEndDateText = MutableLiveData<String>("")
    val choiceRecruitEndDateText: LiveData<String> get() = _choiceRecruitEndDateText

    // 활동 시작일 날짜 텍스트
    private var _choiceActivityStartDateText = MutableLiveData<String>("")
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

    // 참여 키퍼 리워드 제공 여부
    private var _giveReward = MutableLiveData<Boolean>()
    val giveReward: LiveData<Boolean> get() = _giveReward

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

                _choiceRecruitStartDateText.value = "${getMonthStr(ActivityRecruitModel.recruitStartNowMonth)} ${ActivityRecruitModel.recruitStartNowYear}"
            }
            2 -> {
                if (ActivityRecruitModel.recruitEndNowMonth + 1 == 13) {
                    ActivityRecruitModel.recruitEndNowYear = ActivityRecruitModel.recruitEndNowYear + 1
                    ActivityRecruitModel.recruitEndNowMonth = 1
                } else {
                    ActivityRecruitModel.recruitEndNowMonth = ActivityRecruitModel.recruitEndNowMonth + 1
                }

                _choiceRecruitEndDateText.value = "${getMonthStr(ActivityRecruitModel.recruitEndNowMonth)} ${ActivityRecruitModel.recruitEndNowYear}"
            }
            3 -> {
                if (ActivityRecruitModel.activityStartNowMonth + 1 == 13) {
                    ActivityRecruitModel.activityStartNowYear = ActivityRecruitModel.activityStartNowYear + 1
                    ActivityRecruitModel.activityStartNowMonth = 1
                } else {
                    ActivityRecruitModel.activityStartNowMonth = ActivityRecruitModel.activityStartNowMonth + 1
                }

                _choiceActivityStartDateText.value = "${getMonthStr(ActivityRecruitModel.activityStartNowMonth)} ${ActivityRecruitModel.activityStartNowYear}"
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

                _choiceRecruitStartDateText.value =
                    "${getMonthStr(ActivityRecruitModel.recruitStartNowMonth)} ${ActivityRecruitModel.recruitStartNowYear}"
            }
            2 -> {
                if (ActivityRecruitModel.recruitEndNowMonth - 1 == 0) {
                    ActivityRecruitModel.recruitEndNowYear = ActivityRecruitModel.recruitEndNowYear - 1
                    ActivityRecruitModel.recruitEndNowMonth = 12
                } else {
                    ActivityRecruitModel.recruitEndNowMonth =
                        ActivityRecruitModel.recruitEndNowMonth - 1
                }

                _choiceRecruitEndDateText.value =
                    "${getMonthStr(ActivityRecruitModel.recruitEndNowMonth)} ${ActivityRecruitModel.recruitEndNowYear}"
            }
            3 -> {
                if (ActivityRecruitModel.activityStartNowMonth - 1 == 0) {
                    ActivityRecruitModel.activityStartNowYear = ActivityRecruitModel.activityStartNowYear - 1
                    ActivityRecruitModel.activityStartNowMonth = 12
                } else {
                    ActivityRecruitModel.activityStartNowMonth =
                        ActivityRecruitModel.activityStartNowMonth - 1
                }

                _choiceActivityStartDateText.value =
                    "${getMonthStr(ActivityRecruitModel.activityStartNowMonth)} ${ActivityRecruitModel.activityStartNowYear}"
            }
        }
    }

    // 현재 날짜 가져오기
    fun getNowDate() {
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
            "${getMonthStr(ActivityRecruitModel.recruitStartNowMonth)} ${ActivityRecruitModel.recruitStartNowYear}"
        _choiceRecruitEndDateText.value =
            "${getMonthStr(ActivityRecruitModel.recruitStartNowMonth)} ${ActivityRecruitModel.recruitStartNowYear}"
        _choiceActivityStartDateText.value =
            "${getMonthStr(ActivityRecruitModel.recruitStartNowMonth)} ${ActivityRecruitModel.recruitStartNowYear}"
    }

    private fun getMonthStr(month: Int): String {
        return when (month) {
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

    /**
     * type
     * 1: 모집 시작일
     * 2: 모집 종료일
     * 3: 활동 시작일
     */
    // 해당월 일자 list를 가져옴
    fun getDayInMonthArray(type: Int): ArrayList<String> {
        val dayList = arrayListOf<String>()

        val calendar = Calendar.getInstance()
        when(type) {
            1 -> {
                calendar.set(Calendar.YEAR, ActivityRecruitModel.recruitStartNowYear)
                calendar.set(Calendar.MONTH, ActivityRecruitModel.recruitStartNowMonth - 1)
            }
            2 -> {
                calendar.set(Calendar.YEAR, ActivityRecruitModel.recruitEndNowYear)
                calendar.set(Calendar.MONTH, ActivityRecruitModel.recruitEndNowMonth - 1)
            }
            3 -> {
                calendar.set(Calendar.YEAR, ActivityRecruitModel.activityStartNowYear)
                calendar.set(Calendar.MONTH, ActivityRecruitModel.activityStartNowMonth - 1)
            }
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1)


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

    fun findGeoPoint(address: String?) {
        val loc = Location("")
        val coder = Geocoder(ContextUtil.context)
        var addr: List<Address>? = null // 한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 설정
        try {
            addr = coder.getFromLocationName(address!!, 5)
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } // 몇개 까지의 주소를 원하는지 지정 1~5개 정도가 적당
        if (addr != null) {
            for (i in addr.indices) {
                val lating: Address = addr[i]
                val lat: Double = lating.getLatitude() // 위도가져오기
                val lon: Double = lating.getLongitude() // 경도가져오기

                loc.latitude = lat
                loc.longitude = lon
            }
        }

        setLocationInfo(address ?: "", loc)
    }

    // 지도 검색 결과값
    fun setSearchMapResult(value: String) {
        _searchMap.postValue(value)
    }

    // 주소 정보 저장
    private fun setLocationInfo(address: String, location: Location) {
        ActivityRecruitModel.location.address = address
        ActivityRecruitModel.location.latitude = location.latitude
        ActivityRecruitModel.location.longitude = location.longitude
    }

    // 주소 정보 가져오기
    fun getLocationInfo(): ActivityRegisterLocationDto {
        return ActivityRecruitModel.location
    }

    // 클릭된 날짜 저장
    fun setRecruitStartDateClickPosition(pos: Int) {
        ActivityRecruitModel.recruitStartClickedPosition = pos
        ActivityRecruitModel.recruitStartNowDate = pos
    }

    // 모집 시작일 클릭된 날짜 불러오기
    fun getRecruitStartDateClickPosition(): Int {
        return ActivityRecruitModel.recruitStartClickedPosition
    }

    // 모집 시작일 클릭된 날짜 저장
    fun setRecruitEndDateClickPosition(pos: Int) {
        ActivityRecruitModel.recruitEndClickedPosition = pos
    }

    // 모집 시작일 클릭된 날짜 저장
    fun setRecruitStartDateNowDate(date: Int) {
        ActivityRecruitModel.recruitStartNowDate = date
    }

    // 모집 시작일 클릭된 년, 월 불러오기
    fun getRecruitStartClickedDate(): String {
        return ActivityRecruitModel.recruitStartClickedDate
    }

    // 모집 시작일 클릭된 년 월 저장
    fun setRecruitStartClickedDate(date: String) {
        ActivityRecruitModel.recruitStartClickedDate = date
    }

    // 모집 종료일 클릭된 날짜 불러오기
    fun getRecruitEndDateClickPosition(): Int {
        return ActivityRecruitModel.recruitEndClickedPosition
    }

    // 모집 종료일 클릭된 날짜 저장
    fun setRecruitEndDateNowDate(date: Int) {
        ActivityRecruitModel.recruitEndNowDate = date
    }

    // 모집 종료일 클릭된 년, 월 불러오기
    fun getRecruitEndClickedDate(): String {
        return ActivityRecruitModel.recruitEndClickedDate
    }

    // 모집 종료일 클릭된 년 월 저장
    fun setRecruitEndClickedDate(date: String) {
        ActivityRecruitModel.recruitEndClickedDate = date
    }

    // 활동 시작일 클릭된 날짜 저장
    fun setActivityStartDateClickPosition(pos: Int) {
        ActivityRecruitModel.activityStartClickedPosition = pos
    }

    // 활동 시작일 클릭된 날짜 불러오기
    fun getActivityStartDateClickPosition(): Int {
        return ActivityRecruitModel.activityStartClickedPosition
    }

    // 활동 시작일 프로젝트명 길이 텍스트 변경
    fun onChangedProjectNameEditText(str: String) {
        ActivityRecruitModel.projectName = str
        _projectNameLength.postValue(str.length)
    }

    // 활동 시작일 클릭된 날짜 저장
    fun setActivityStartDateNowDate(date: Int) {
        Timber.e("setActivityStartDateNowDate $date")
        ActivityRecruitModel.activityStartNowDate = date
    }

    // 활동 시작일 클릭된 년, 월 불러오기
    fun getActivityStartClickedDate(): String {
        return ActivityRecruitModel.activityStartClickedDate
    }

    // 활동 시작일 클릭된 년 월 저장
    fun setActivityStartClickedDate(date: String) {
        ActivityRecruitModel.activityStartClickedDate = date
    }

    // 활동 시작일 클릭된 시간 저장
    fun setActivityStartTimeHour(hour: Int, amPm: String) {
        if(amPm == "AM") {
            // 오전
            ActivityRecruitModel.activityStartTimeHour = hour
        } else {
            // 오후
            ActivityRecruitModel.activityStartTimeHour = hour + 12
        }
    }

    // 활동 시작일 클릭된 분 저장
    fun setActivityStartTimeMinute(minute: Int) {
        ActivityRecruitModel.activityStartTimeMinute = minute
    }

    // 모집 시작일 날짜 시간 가져오기
    fun getRecruitStartDate(): String {
        return "${ActivityRecruitModel.recruitStartNowYear}-${isLengthOne(ActivityRecruitModel.recruitStartNowMonth)}-${isLengthOne(ActivityRecruitModel.recruitStartNowDate)}T00:00:00"
    }

    // 모집 종료일 날짜 시간 가져오기
    fun getRecruitEndDate(): String {
        return "${ActivityRecruitModel.recruitEndNowYear}-${isLengthOne(ActivityRecruitModel.recruitEndNowMonth)}-${isLengthOne(ActivityRecruitModel.recruitEndNowDate)}T23:59:59"
    }

    // 활동 시작일 날짜 시간 가져오기
    fun getActivityStartDate(): String {
        return "${ActivityRecruitModel.activityStartNowYear}-${isLengthOne(ActivityRecruitModel.activityStartNowMonth)}-${isLengthOne(ActivityRecruitModel.activityStartNowDate)}T${isLengthOne(ActivityRecruitModel.activityStartTimeHour ?: 1)}:${isLengthOne(ActivityRecruitModel.activityStartTimeMinute ?: 0)}:00"
    }

    // 한자리 수면 앞에 0 붙여줌
    private fun isLengthOne(text: Int): String {
        return if(text.toString().length == 1) {
            "0$text"
        } else {
            text.toString()
        }
    }

    // 임서저장 활성화 여부 확인
    fun isLoadTempData(): Boolean {
        return ActivityRecruitModel.isLoadTempData
    }

    // 임시저장 활성화 여부 변경
    fun setIsLoadTempData(flag: Boolean) {
        ActivityRecruitModel.isLoadTempData = flag
    }

    // 참여 키퍼 리워드 여부 확인
    fun isGiveReward(): Boolean {
        return ActivityRecruitModel.isGiveReward
    }

    // 참여 키퍼 리워드 여부 변경
    fun setIsGiveReward(flag: Boolean) {
        _giveReward.postValue(flag)
        ActivityRecruitModel.isGiveReward = flag
    }

    /**
     * 1: 카셰어링 연결 예정
     * 2: 단체차량 대절 예정
     * 3: 교통편 제공 없음
     */
    // 교통 안내 여부 값 변경
    fun setTrafficGuideValue(value: Int) {
        ActivityRecruitModel.trafficGuide = value
        _trafficGuideValue.postValue(value)
    }

    // 교통 안내 여부 값 가져오기
    fun getGuideTrafficStringValue(): String {
        return when(ActivityRecruitModel.trafficGuide) {
            1 -> "카셰어링 연결 예정"
            2 -> "단체차량 대절 예정"
            3 -> "교통편 제공 없음"
            else -> ""
        }
    }

    // 교통 안내 여부 값 가져오기
    fun getGuideTrafficValue(): Int {
        return ActivityRecruitModel.trafficGuide
    }

    // 프로젝트명 가져오기
    fun getProjectName(): String {
        return ActivityRecruitModel.projectName
    }

    // 모집 인원 변경
    fun setQuota(value: Int) {
        ActivityRecruitModel.quota = value
    }

    // 모집 인원 가져오기
    fun getQuota(): Int {
        return ActivityRecruitModel.quota ?: 0
    }

    // 활동 프로그램 안내 변경
    fun setGuideActivity(text: String) {
        ActivityRecruitModel.guideActivity = text
    }

    // 활동 프로그램 안내 가져오기
    fun getGuideActivity(): String {
        return ActivityRecruitModel.guideActivity
    }

    // 준비물 가져오기
    fun getMaterial(): String {
        return ActivityRecruitModel.material
    }

    // 준비물 변경
    fun setMaterial(text: String) {
        ActivityRecruitModel.material = text
    }

    // 제공 리워드 가져오기
    fun getGiveRewardStr(): String {
        return ActivityRecruitModel.giveReward
    }

    // 제공 리워드 변경
    fun setGiveReward(text: String) {
        ActivityRecruitModel.giveReward = text
    }

    // 기타 안내 사항 가져오기
    fun getOtherGuide(): String {
        return ActivityRecruitModel.otherGuide
    }

    // 기타 안내 사항 변경
    fun setOtherGuide(text: String) {
        ActivityRecruitModel.otherGuide = text
    }

    /**
     * 1: 연안쓰레기
     * 2: 부유쓰레기
     * 3: 침적쓰레기
     * 4: 기타
     */
    // 모집 카테고리 값 변경
    fun setRecruitCategoryValue(value: Int) {
        ActivityRecruitModel.recruitCategory = value
        _recruitCategory.postValue(value)
    }

    // 모집 카테고리 값 가져오기
    fun getRecruitCategoryStringValue(): String {
        return when(ActivityRecruitModel.recruitCategory) {
            1 -> "COASTAL"
            2 -> "FLOATING"
            3 -> "DEPOSITED"
            4 -> "ETC"
            else -> ""
        }
    }

    // 모집 카테고리 값 가져오기
    fun getRecruitCategoryValue(): Int {
        return ActivityRecruitModel.recruitCategory
    }

    /**
     * 1: 서해번쩍
     * 2: 동해번쩍
     * 3: 남해번쩍
     * 4: 제주번쩍
     * 5: 기타
     */
    // 모집 위치 값 변경
    fun setRecruitLocationValue(value: Int) {
        ActivityRecruitModel.recruitLocation = value
        _recruitLocation.postValue(value)
    }

    // 모집 위치 값 가져오기
    fun getRecruitLocationStringValue(): String {
        return when(ActivityRecruitModel.recruitLocation) {
            1 -> "WEST"
            2 -> "EAST"
            3 -> "SOUTH"
            4 -> "JEJU"
            5 -> "ETC"
            else -> ""
        }
    }

    // 모집 위치 값 가져오기
    fun getRecruitLocationValue(): Int {
        return ActivityRecruitModel.recruitLocation
    }

    // 모집 시작일, 종료일 컵포넌트 선택값
    fun setClickedRecruitComponent(value: Int) {
        _clickedRecruitComponent.postValue(value)
    }

    // 활동 시작일, 종료일 컵포넌트 선택값
    fun setClickedActivityComponent(value: Int) {
        _clickedActivityComponent.postValue(value)
    }

    // 모집 시작일 캘린더 날짜 선택 시 초기화
    private fun clearRecruitStartDateInfo() {
        setRecruitStartDateClickPosition(-1)
        setRecruitStartDateNowDate(-1)
    }

    // 모집 종료일 캘린더 날짜 선택 시 초기화
    private fun clearRecruitEndDateInfo() {
        setRecruitEndDateClickPosition(-1)
        setRecruitEndDateNowDate(-1)
    }

    // 활동 시작일 캘린더 날짜 선택 시 초기화
    private fun clearActivityStartDateInfo() {
        setActivityStartDateClickPosition(-1)
        setActivityStartDateNowDate(-1)
    }

    // 필수 정보가 모두 들어갔는지 여부 체크
    fun isExistNeedData(): Boolean {
        Timber.e("activityStartTimeHour ${ActivityRecruitModel.activityStartTimeHour}")
        Timber.e("activityStartTimeMinute ${ActivityRecruitModel.activityStartTimeMinute}")
        return ActivityRecruitModel.projectName != "" && ActivityRecruitModel.location.address != "" && ActivityRecruitModel.quota != null && ActivityRecruitModel.recruitStartClickedDate != "" && ActivityRecruitModel.recruitEndClickedDate != "" && ActivityRecruitModel.activityStartClickedDate != "" && ActivityRecruitModel.activityStartTimeHour != null && ActivityRecruitModel.activityStartTimeMinute != null && ActivityRecruitModel.guideActivity != ""
    }

    // 활동 모집 데이터 초기화 (필수로 지워줘야 하는거만 포함, 알아서 초기화 되는건 안함)
    fun clearTempData() {
        clearRecruitStartDateInfo()
        clearRecruitEndDateInfo()
        clearActivityStartDateInfo()
        ActivityRecruitModel.isGiveReward = false
        ActivityRecruitModel.projectName = ""
        ActivityRecruitModel.quota = null
        ActivityRecruitModel.trafficGuide = 0
        ActivityRecruitModel.recruitCategory = 1
        ActivityRecruitModel.recruitLocation = 1
        ActivityRecruitModel.location = ActivityRegisterLocationDto()
        ActivityRecruitModel.guideActivity = ""
        ActivityRecruitModel.material = ""
        ActivityRecruitModel.giveReward = ""
        ActivityRecruitModel.otherGuide = ""
    }
}