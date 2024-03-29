package com.letspl.oceankeeper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letspl.oceankeeper.data.dto.LoginInfo
import com.letspl.oceankeeper.data.model.LoginModel
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.repository.LoginRepositoryImpl
import com.letspl.oceankeeper.util.EntryPoint
import com.letspl.oceankeeper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepositoryImpl: LoginRepositoryImpl) :
    ViewModel() {

    private var _onLoginResult = MutableLiveData<Boolean?>()
    val onLoginResult: LiveData<Boolean?>
        get() = _onLoginResult

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 네이버 계정 정보 조회 요청
    fun getNaverUserInfo(token: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    loginRepositoryImpl.getNaverUserInfo("Bearer $token")
                }.fold(onSuccess = {
                    if (it.isSuccessful) {
                        it.body()?.response.let { data ->
                            data?.let {
                                LoginModel.login.provider = "naver"
                                LoginModel.login.providerId = it.id
                                LoginModel.login.nickname = it.nickname
                                LoginModel.login.email = it.email
                                LoginModel.login.profile = it.profileImage.replace("\\", "")
                            }
                        }
                        loginUser("NAVER")
                    } else {
                        val errorJsonObject = ParsingErrorMsg.parsingFromStringToJson(
                            it.errorBody()?.string() ?: ""
                        )
                        if (errorJsonObject != null) {
                            val errorMsg =
                                ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                            _errorMsg.postValue(errorMsg)
                        }
                    }
                }, onFailure = {
                    _errorMsg.postValue(it.message)
                    Timber.e("it throwable ${it.message}")
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLoginInfo(): LoginInfo {
        return LoginModel.login
    }

    // 로그인 시도 회원가입 유무 확인 후 분기 처리
    fun loginUser(loginType: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                loginRepositoryImpl.loginUser(
                    LoginModel.login.deviceToken,
                    LoginModel.login.provider,
                    LoginModel.login.providerId
                )
            }.fold(onSuccess = {
                if (it.isSuccessful) {
                    EntryPoint.login = loginType

                    UserModel.userInfo.let { data ->
                        it.body()?.let { serverData ->
                            data.token.accessToken = serverData.response.token.accessToken
                            data.token.accessTokenExpiresIn =
                                serverData.response.token.accessTokenExpiresIn
                            data.token.grantType = serverData.response.token.grantType
                            data.token.refreshToken = serverData.response.token.refreshToken
                            data.user.id = serverData.response.user.id
                            data.user.nickname = serverData.response.user.nickname
                            data.user.profile = serverData.response.user.profile
                        }
                    }
                    _onLoginResult.postValue(true)
                } else {
                    _onLoginResult.postValue(false)
                }
            }, onFailure = {
                _errorMsg.postValue(it.message)
            })
        }
    }

    fun sendErrorMsg(msg: String) {
        _errorMsg.postValue(msg)
    }

    // liveData 값 초기화
    fun clearLiveData() {
        _onLoginResult.postValue(null)
    }

    fun clearErrorMsg() {
        _errorMsg.postValue("")
    }
}