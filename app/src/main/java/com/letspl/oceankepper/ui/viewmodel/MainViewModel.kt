package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.ComingScheduleItem
import com.letspl.oceankepper.data.dto.GetComingScheduleItem
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

    // 다가오는 일정 슬라이드 위치
    private var _slidePosition = MutableLiveData<Int>(0)
    val slidePosition: LiveData<Int> get() = _slidePosition


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

    fun onChangeSlidePosition(pos: Int) {
        _slidePosition.value = pos
    }

}