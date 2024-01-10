package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.NotificationItemDto
import com.letspl.oceankepper.data.model.LoginModel
import com.letspl.oceankepper.data.model.NotificationModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.LoginRepositoryImpl
import com.letspl.oceankepper.data.repository.NotificationRepositoryImpl
import com.letspl.oceankepper.data.repository.PrivacyRepositoryImpl
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val loginRepositoryImpl: LoginRepositoryImpl, private val notificationRepositoryImpl: NotificationRepositoryImpl, private val privacyRepositoryImpl: PrivacyRepositoryImpl): ViewModel() {

    private var _getTermsResult = MutableLiveData<String>()
    val getTermsResult: LiveData<String> get() = _getTermsResult

    private var _getNotificationAlarmResult = MutableLiveData<Boolean?>(null)
    val getNotificationAlarmResult: LiveData<Boolean?> get() = _getNotificationAlarmResult

    private var _getNotificationListResult = MutableLiveData<List<NotificationItemDto>>()
    val getNotificationListResult: LiveData<List<NotificationItemDto>> get() = _getNotificationListResult

    private var _postNotificationAlarmResult = MutableLiveData<Boolean?>(null)
    val postNotificationAlarmResult: LiveData<Boolean?> get() = _postNotificationAlarmResult

    private var _postWithDrawResult = MutableLiveData<Boolean>()
    val postWithDrawResult: LiveData<Boolean> get() = _postWithDrawResult

    private var _postLogoutResult = MutableLiveData<Boolean>()
    val postLogoutResult: LiveData<Boolean> get() = _postLogoutResult

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 로그아웃
    fun postLogout() {
        viewModelScope.launch {
            loginRepositoryImpl.logoutUser(
                LoginModel.login.deviceToken,
                LoginModel.login.provider,
                LoginModel.login.providerId
            ).let {
                if(it.isSuccessful) {
                    _postLogoutResult.postValue(true)
                } else {
                    val errorJsonObject =
                        ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                    if (errorJsonObject != null) {
                        val errorMsg = ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                        _errorMsg.postValue(errorMsg)
                    }
                }
            }
        }
    }

    // 탈퇴하기
    fun postWithdraw() {
        viewModelScope.launch {
            loginRepositoryImpl.withdrawAccount(
                LoginModel.login.deviceToken,
                LoginModel.login.provider,
                LoginModel.login.providerId
            ).let {
                if(it.isSuccessful) {
                    _postWithDrawResult.postValue(true)
                } else {
                    val errorJsonObject =
                        ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                    if (errorJsonObject != null) {
                        val errorMsg = ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                        _errorMsg.postValue(errorMsg)
                    }
                }
            }
        }
    }

    // 알림 설정
    fun postNotificationAlarm(alarm: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            notificationRepositoryImpl.postNotificationAlarm(
                alarm,
                UserModel.userInfo.user.id
            ).let {
                if(it.isSuccessful) {
                    _postNotificationAlarmResult.postValue(true)
                } else {
                    _postNotificationAlarmResult.postValue(false)
                }
            }
        }
    }

    // 알림 설정 가져오기
    fun getNotificationAlarm() {
        CoroutineScope(Dispatchers.IO).launch {
            notificationRepositoryImpl.getNotificationAlarm(
                UserModel.userInfo.user.id
            ).let {
                if(it.isSuccessful) {
                    _getNotificationAlarmResult.postValue(it.body()?.response?.alarm)
                } else {
                    _getNotificationAlarmResult.postValue(false)
                }
            }
        }
    }

    // 알림 리스트  가져오기
    fun getNotificationList() {
        if (!NotificationModel.lastMemo) {
            CoroutineScope(Dispatchers.IO).launch {
                notificationRepositoryImpl.getNotificationList(
                    10,
                    NotificationModel.lastMemoId,
                    UserModel.userInfo.user.id
                ).let {
                    if (it.isSuccessful) {
                        val notificationItemArr = arrayListOf<NotificationItemDto>()
                        val response = it.body()?.response

                        response?.data?.forEach { item ->
                            notificationItemArr.add(
                                NotificationItemDto(
                                    item.id,
                                    item.contents,
                                    item.createdAt,
                                    item.read
                                )
                            )
                        }

                        if (response?.meta?.last == true) {
                            NotificationModel.lastMemo = true
                            if (response.data.isNotEmpty()) {
                                NotificationModel.lastMemoId =
                                    response.data[response.data.size - 1].id
                            }
                        }

                        _getNotificationListResult.postValue(notificationItemArr)
                    } else {
                        val errorJsonObject =
                            ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                        if (errorJsonObject != null) {
                            val errorMsg =
                                ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                            _errorMsg.postValue(errorMsg)
                        }
                    }
                }
            }
        }
    }

    // 이용약관 가져오기
    fun getPrivacyPolicy() {
        CoroutineScope(Dispatchers.IO).launch {
            privacyRepositoryImpl.getPrivacyPolicy().let {
                if(it.isSuccessful) {
                    _getTermsResult.postValue(it.body()?.response?.contents)
                } else {
                    val errorJsonObject =
                        ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                    if (errorJsonObject != null) {
                        val errorMsg = ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                        _errorMsg.postValue(errorMsg)
                    }
                }
            }
        }
    }

    fun clearLiveData() {
        NotificationModel.lastMemo = false
        NotificationModel.lastMemoId = null
        _getNotificationAlarmResult.postValue(null)
        _postNotificationAlarmResult.postValue(null)
    }

    fun clearErrorMsg() {
        _errorMsg.postValue("")
    }

}