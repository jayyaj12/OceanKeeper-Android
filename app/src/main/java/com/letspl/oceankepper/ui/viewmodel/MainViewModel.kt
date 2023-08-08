package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.ComingScheduleItem
import com.letspl.oceankepper.data.dto.MyActivityItem
import com.letspl.oceankepper.data.model.MainModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.MainRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
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

                }
            }
        }
    }

    // 활동 조회 첫 조회시에는 activityId 안 보냄
    fun getMyActivities(garbageCategory: String, locationTag: String, size: Int) {
        viewModelScope.launch {
            mainRepositoryImpl.getMyActivity("Bearer ${UserModel.userInfo.token.accessToken}", garbageCategory, locationTag, size).let {
                if(it.isSuccessful) {
                    _getMyActivityResult.postValue(it.body()?.response?.activities)
                } else {

                }
            }
        }
    }
    // 활동 조회 첫 조회
    fun getMyActivities(activityId: String, garbageCategory: String, locationTag: String, size: Int) {
        viewModelScope.launch {
            mainRepositoryImpl.getMyActivity("Bearer ${UserModel.userInfo.token.accessToken}", activityId, garbageCategory, locationTag, size).let {
                if(it.isSuccessful) {
                    _getMyActivityResult.postValue(it.body()?.response?.activities)
                } else {

                }
            }
        }
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

    fun getActivityStatus(): String {
        return when (_activityStatusPosition.value) {
            1 -> "open"
            2 -> "closed"
            else -> ""
        }
    }

    // 지역 모달 선택 결과값
    fun getAreaModalClickWord(): String {
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

    // 종류 모달 선택 결과값
    fun getGarbageCategoryModalClickWord(): String {
        return when (MainModel.garbageCategoryPosition) {
            -1 -> "종류"
            0 -> "연안쓰레기"
            1 -> "부유쓰레기"
            2 -> "침적쓰레기"
            3 -> "기타"
            else -> ""
        }
    }
}