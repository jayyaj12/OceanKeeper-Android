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
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepositoryImpl: LoginRepositoryImpl) :
    ViewModel() {

    private var _onLoginResult = MutableLiveData<Boolean?>()
    val onLoginResult: LiveData<Boolean?>
        get() = _onLoginResult

    // 네이버 계정 정보 조회 요청
    fun getNaverUserInfo(token: String) {
        viewModelScope.launch {
            val data = loginRepositoryImpl.getNaverUserInfo("Bearer $token")
            when (data.isSuccessful) {
                true -> {
                    data.body()?.response.let { data ->
                        data?.let {
                            LoginModel.login.provider = "naver"
                            LoginModel.login.providerId = it.id
                            LoginModel.login.nickname = it.nickname
                            LoginModel.login.email = it.email
                            LoginModel.login.profile = it.profileImage.replace("\\", "")
                        }
                    }
                    loginUser()
                }
                else -> Timber.e("data is not Successful")
            }
        }
    }

    fun getLoginInfo(): LoginInfo {
        return LoginModel.login
    }

    // 로그인 시도 회원가입 유무 확인 후 분기 처리
    fun loginUser() {
        viewModelScope.launch {
            val data = loginRepositoryImpl.loginUser(
                LoginModel.login.deviceToken,
                LoginModel.login.provider,
                LoginModel.login.providerId
            )

            if(data.body() != null) {
                UserModel.userInfo.token.accessToken = data.body()?.response?.token?.accessToken.toString()
                UserModel.userInfo.token.accessTokenExpiresIn = data.body()?.response?.token?.accessTokenExpiresIn.toString()
                UserModel.userInfo.token.grantType = data.body()?.response?.token?.grantType.toString()
                UserModel.userInfo.token.refreshToken = data.body()?.response?.token?.refreshToken.toString()
                UserModel.userInfo.user.id = data.body()?.response?.user?.id.toString()
                UserModel.userInfo.user.nickname = data.body()?.response?.user?.nickname.toString()
            }

            Timber.e("data.isSuccessful ${data.isSuccessful}")

            when(data.isSuccessful) {
                true -> {
                    // 로그인 성공
                    _onLoginResult.postValue(true)
                }
                else -> {
                    // 로그인 정보 없음
                    _onLoginResult.postValue(false)
                }
            }
        }
    }

    // liveData 값 초기화
    fun clearLiveData() {
        _onLoginResult.postValue(null)
    }
}