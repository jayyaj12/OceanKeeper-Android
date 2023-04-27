package com.letspl.oceankepper.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.BuildConfig
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
class LoginViewModel @Inject constructor(private val loginRepositoryImpl: LoginRepositoryImpl): ViewModel() {

    // 로그인
    fun onLoginAccount(provider: String, providerId: String) {
        viewModelScope.launch {
            loginRepositoryImpl.postAppLogin(provider, providerId)
        }
    }

    // 네이버 계정 정보 조회 요청
    fun getNaverUserInfo(token: String) {
        viewModelScope.launch {
            val data = loginRepositoryImpl.getNaverUserInfo("Bearer $token")
            when (data.isSuccessful) {
                true -> {
                    data.body()?.response.let { data ->
                        data?.let {

                        }
                    }
                    Timber.e("data ${data.body()?.response}")
                }
                else -> Timber.e("data is not Successful")
            }
        }
    }

    fun onLoginNaver() {

    }

}