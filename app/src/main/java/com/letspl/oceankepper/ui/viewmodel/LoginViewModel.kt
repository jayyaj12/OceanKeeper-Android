package com.letspl.oceankepper.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.LoginInfo
import com.letspl.oceankepper.data.model.LoginModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.LoginRepositoryImpl
import com.letspl.oceankepper.ui.view.BaseActivity
import com.letspl.oceankepper.util.ContextUtil
import com.letspl.oceankepper.util.ParsingErrorMsg
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepositoryImpl: LoginRepositoryImpl) :
    ViewModel() {

    private var _onLoginResult = MutableLiveData<Boolean>()
    val onLoginResult: LiveData<Boolean>
        get() = _onLoginResult

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 네이버 계정 정보 조회 요청
    fun getNaverUserInfo(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            loginRepositoryImpl.getNaverUserInfo("Bearer $token").let {
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
                    loginUser()
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

    fun getLoginInfo(): LoginInfo {
        return LoginModel.login
    }

    // 로그인 시도 회원가입 유무 확인 후 분기 처리
    fun loginUser() {
        CoroutineScope(Dispatchers.IO).launch {
            loginRepositoryImpl.loginUser(
                LoginModel.login.deviceToken,
                LoginModel.login.provider,
                LoginModel.login.providerId
            ).let {
                if(it.isSuccessful) {
                    UserModel.userInfo.let { data ->
                        it.body()?.let {serverData ->
                            data.token.accessToken =
                                serverData.response.token.accessToken
                            data.token.accessTokenExpiresIn =
                                serverData.response.token.accessTokenExpiresIn
                            data.token.grantType =
                                serverData.response.token.grantType
                            data.token.refreshToken =
                                serverData.response.token.refreshToken
                            data.user.id = serverData.response.user.id
                            data.user.nickname = serverData.response.user.nickname
                            data.user.profile = serverData.response.user.profile
                        }
                    }
                    _onLoginResult.postValue(true)
                } else {
                     _onLoginResult.postValue(false)
                }
            }
        }
    }

    // liveData 값 초기화
    fun clearLiveData() {
        _onLoginResult.postValue(null)
    }

    fun clearErrorMsg() {
        _errorMsg.postValue("")
    }
}