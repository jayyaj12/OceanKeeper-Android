package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.ComingScheduleItem
import com.letspl.oceankepper.data.dto.GetComingScheduleItem
import com.letspl.oceankepper.data.dto.MyActivityItem
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


    // 다가오는 일정 데이터 조회
    fun getComingSchedule() {
        viewModelScope.launch {
            Timber.e("UserModel.userInfo.user.id ${UserModel.userInfo.user.id}")
            mainRepositoryImpl.getComingSchedule("Bearer ${UserModel.userInfo.token.accessToken}", UserModel.userInfo.user.id).let {
                if(it.isSuccessful) {
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

    fun onChangeSlidePosition(pos: Int) {
        _slidePosition.value = pos
    }

    fun onChangeActivityStatusPosition(pos: Int) {
        _activityStatusPosition.value = pos
    }

    fun getActivityStatus(): String {
        return when(_activityStatusPosition.value) {
            1 -> "open"
            2 -> "closed"
            else -> ""
        }
    }

}