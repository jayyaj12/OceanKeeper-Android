package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.ComingScheduleItem
import com.letspl.oceankepper.data.dto.GetMyActivityDetailItem
import com.letspl.oceankepper.data.dto.GetMyActivityDetailResponse
import com.letspl.oceankepper.data.dto.MyActivityItem
import com.letspl.oceankepper.data.model.ActivityRecruitModel
import com.letspl.oceankepper.data.model.MainModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.MainRepositoryImpl
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepositoryImpl: MainRepositoryImpl): ViewModel() {

    // 다가오는 일정 데이터 조회 결과
    private var _getComingScheduleResult = MutableLiveData<List<ComingScheduleItem>>()
    val getComingScheduleResult: LiveData<List<ComingScheduleItem>> get() = _getComingScheduleResult

    // 내 활동 보기 조회 결과
    private var _getMyActivityResult = MutableLiveData<List<MyActivityItem>>()
    val getMyActivityResult: LiveData<List<MyActivityItem>> get() = _getMyActivityResult

    // 다가오는 일정 슬라이드 위치
    private var _slidePosition = MutableLiveData<Int>(0)
    val slidePosition: LiveData<Int> get() = _slidePosition

    // 전체, 진행예정 활동, 종료된 활동 선택 위치
    private var _activityStatusPosition = MutableLiveData<Int>(0)
    val activityStatusPosition: LiveData<Int> get() = _activityStatusPosition

    // 지역 팝업 선택 값
    private var _areaModalClickPosition = MutableLiveData<Int>(-1)
    val areaModalClickPosition: LiveData<Int> get() = _areaModalClickPosition

    // 쓰레기 종류 팝업 선택 값
    private var _garbageCategoryModalClickPosition = MutableLiveData<Int>(-1)
    val garbageCategoryModalClickPosition: LiveData<Int> get() = _garbageCategoryModalClickPosition

    // 활동 조회 결과
    private var _activityDetailSelectResult = MutableLiveData<GetMyActivityDetailResponse?>(null)
    val activityDetailSelectResult: LiveData<GetMyActivityDetailResponse?> get() = _activityDetailSelectResult

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg
    // 다가오는 일정 데이터 조회
    fun getComingSchedule() {
        viewModelScope.launch {
            Timber.e("UserModel.userInfo.user.id ${UserModel.userInfo.user.id}")
            mainRepositoryImpl.getComingSchedule(
                "Bearer ${UserModel.userInfo.token.accessToken}",
                UserModel.userInfo.user.id
            ).let {
                if (it.isSuccessful) {
                    _getComingScheduleResult.postValue(it.body()?.response?.activities)
                } else {
                    val errorJsonObject = ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                    if(errorJsonObject != null) {
                        val errorMsg = ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                        _errorMsg.postValue(errorMsg)
                    }
                }
            }
        }
    }

    // 활동 조회 첫 조회
    fun getMyActivities(garbageCategory: String?, locationTag: String?, size: Int, status: String?) {
        viewModelScope.launch {
            // 활동 조회 첫 조회시에는 activityId 안 보냄
            viewModelScope.launch {
                // 마지막 액티비티가 아닌 경우에만 조회
                if(!MainModel.lastActivity) {
                    mainRepositoryImpl.getMyActivity(
                        "Bearer ${UserModel.userInfo.token.accessToken}",
                        MainModel.lastActivityId,
                        garbageCategory,
                        locationTag,
                        size,
                        status
                    ).let {
                        if (it.isSuccessful) {
                            val activities =it.body()?.response?.activities!!
                            MainModel.activityList.addAll(it.body()?.response?.activities!!)
                            _getMyActivityResult.postValue(MainModel.activityList)
                            if (activities.isNotEmpty()) {
                                MainModel.lastActivity = it.body()?.response?.meta?.last!!
                                MainModel.lastActivityId =
                                    activities[activities.size - 1].activityId
                            } else {
                                MainModel.lastActivity = false
                                MainModel.lastActivityId = null
                            }
                        } else {
                            val errorJsonObject = ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                            if(errorJsonObject != null) {
                                val errorMsg = ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                                _errorMsg.postValue(errorMsg)
                            }
                        }
                    }
                }
            }
        }
    }

    // 활동 조회 첫 조회
    fun getMyActivityDetail(activityId: String) {
        viewModelScope.launch {
            mainRepositoryImpl.getMyActivityDetail("Bearer ${UserModel.userInfo.token.accessToken}", activityId).let {
                if(it.isSuccessful) {
                    _activityDetailSelectResult.postValue(it.body())
                    setClickedActivityItem(it.body()?.response!!)
                } else{
                    val errorJsonObject = ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                    if(errorJsonObject != null) {
                        val errorMsg = ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                        _errorMsg.postValue(errorMsg)
                    }
                }
            }
        }
    }

    // 모집 기간인지 확인
    fun isRecruitmentTerms(startRecruitmentDate: String?, endRecruitmentDate: String?): Boolean {
        return if(startRecruitmentDate != null && endRecruitmentDate != null) {
            (getDateDiff(startRecruitmentDate) > 0 && getDateDiff(endRecruitmentDate) <= 0)
        } else {
            false
        }
    }

    // 활동 종료일 이후 7일까지는 신청자 관리 버튼 표시되어야 함
    private fun getDateDiff(startAt: String): Long {
        val today = Calendar.getInstance()
        val date = SimpleDateFormat("yyyy-MM-dd").parse(startAt)
        Timber.e("startAt $startAt")

        return (today.time.time - date.time) / (60 * 60 * 24 * 1000)
    }

    // 활동 상태를 변경 하거나, 카테고리 변경 시 마지막 활동 리스트 여부를 초기화
    fun setLastActivity(flag: Boolean) {
        MainModel.lastActivityId = null
        MainModel.lastActivity = flag
    }

    // 자동 슬라이드 위치 변경
    fun onChangeSlidePosition(pos: Int) {
        _slidePosition.value = pos
    }

    // 자동 슬라이드 위치 변경
    fun onChangeActivityStatusPosition(pos: Int) {
        _activityStatusPosition.value = pos
    }

    // 지역 모달 선택 아이템 위치값 변경
    fun onChangeAreaModalClickPosition(pos: Int) {
        _areaModalClickPosition.value = pos
    }

    // 지역 모달 저장 버튼 클릭 시 값 저장
    fun saveAreaModalClickPosition() {
        MainModel.areaPosition = _areaModalClickPosition.value!!
        MainModel.tempAreaPosition = _areaModalClickPosition.value!!
        _areaModalClickPosition.value = MainModel.areaPosition
    }

    // 지역 모달 닫기 버튼 클릭 시 최초 temp 로 저장해둔 값으로 다시 저장한다.
    fun closeAreaModal() {
        _areaModalClickPosition.value = MainModel.tempAreaPosition
    }

    // 종류 모달 선택 아이템 위치값 변경
    fun onChangeGarbageCategoryModalClickPosition(pos: Int) {
        _garbageCategoryModalClickPosition.value = pos
    }

    // 종류 모달 저장 버튼 클릭 시 값 저장
    fun saveGarbageCategoryModalClickPosition() {
        MainModel.garbageCategoryPosition = _garbageCategoryModalClickPosition.value!!
        MainModel.tempGarbageCategoryPosition = MainModel.tempGarbageCategoryPosition
        _garbageCategoryModalClickPosition.value = MainModel.garbageCategoryPosition
    }

    // 종류 모달 닫기 버튼 클릭 시 최초 temp 로 저장해둔 값으로 다시 저장한다.
    fun closeGarbageCategoryModal() {
        _areaModalClickPosition.value = MainModel.tempGarbageCategoryPosition
    }

    // 지역 모달 선택 결과값
    fun getAreaModalClickWordKor(): String {
        return when (MainModel.areaPosition) {
            -1 -> "지역"
            0 -> "서해번쩍"
            1 -> "동해번쩍"
            2 -> "남해번쩍"
            3 -> "제주번쩍"
            4 -> "기타"
            else -> ""
        }
    }

    // 지역 모달 선택 결과값
    fun getAreaModalClickWordEng(): String? {
        return when (MainModel.areaPosition) {
            -1 -> null
            0 -> "WEST"
            1 -> "EAST"
            2 -> "SOUTH"
            3 -> "JEJU"
            4 -> "ETC "
            else -> null
        }
    }


    // 지역 모달 선택 결과값
    fun getAreaModalClickWordKor(type: String?): String {
        return when (type) {
            "WEST" -> "서해번쩍"
            "EAST" -> "동해번쩍"
            "SOUTH" -> "남해번쩍"
            "JEJU" -> "제주번쩍"
            "ETC " -> "기타"
            else -> ""
        }
    }

    // 활동 상태 결과값
    fun getActivityStatus(): String? {
        return when (activityStatusPosition.value) {
            0 -> "open"
            1 -> "recruitment-closed"
            2 -> "closed"
            else -> null
        }
    }

    // 종류 모달 선택 결과값
    fun getGarbageCategoryModalClickWordKor(): String {
        return when (MainModel.garbageCategoryPosition) {
            -1 -> "종류"
            0 -> "연안쓰레기"
            1 -> "부유쓰레기"
            2 -> "침적쓰레기"
            3 -> "기타"
            else -> ""
        }
    }

    // 종류 모달 선택 결과값
    fun getGarbageCategoryModalClickWordEng(): String? {
        return when (MainModel.garbageCategoryPosition) {
            -1 -> null
            0 -> "COASTAL"
            1 -> "FLOATING"
            2 -> "DEPOSITED"
            3 -> "ETC"
            else -> ""
        }
    }

    fun getGarbageCategoryKor(type: String?): String {
        return when (type) {
            "COASTAL" -> "연안쓰레기"
            "FLOATING" -> "부유쓰레기"
            "DEPOSITED" -> "침적쓰레기"
            "ETC" -> "기타"
            else -> ""
        }
    }

    // activityId 값 변경
    fun setClickedActivityId(value: String) {
        MainModel.clickedActivityId = value
    }

    fun getClickedActivityId(): String {
        return MainModel.clickedActivityId
    }

    // clickedActivityName 값 변경
    private fun setClickedActivityItem(item: GetMyActivityDetailItem) {
        MainModel.clickedActivityItem = item
    }

    fun getClickedActivityItem(): GetMyActivityDetailItem {
        return MainModel.clickedActivityItem
    }

    fun initGarbageLocationSelected() {
        onChangeGarbageCategoryModalClickPosition(-1)
        onChangeAreaModalClickPosition(-1)
        saveGarbageCategoryModalClickPosition()
        saveAreaModalClickPosition()
    }

    fun clearActivityList() {
        MainModel.activityList.clear()
    }
}