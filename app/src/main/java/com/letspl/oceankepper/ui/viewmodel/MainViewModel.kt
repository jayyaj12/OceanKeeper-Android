package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.MainRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepositoryImpl: MainRepositoryImpl): ViewModel() {

    // 다가오는 일정 데이터 조회
    fun selectComingSchedule() {
        viewModelScope.launch {
            Timber.e("UserModel.userInfo.user.id ${UserModel.userInfo.user.id}")
            mainRepositoryImpl.getComingSchedule(UserModel.userInfo.token.accessToken, UserModel.userInfo.user.id).let {
                if(it.isSuccessful) {

                } else {

                }
            }
        }
    }

}