package com.letspl.oceankepper.util.loginManager

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.letspl.oceankepper.data.model.LoginModel
import com.letspl.oceankepper.ui.viewmodel.LoginViewModel
import com.letspl.oceankepper.util.ContextUtil
import kotlinx.coroutines.*
import timber.log.Timber
import java.security.MessageDigest
import javax.inject.Inject


class KakaoLoginManager @Inject constructor(private val loginViewModel: LoginViewModel) {
    // 로그인 공통 callback 구성

    fun onClickedKakaoLogin() {
        Timber.e(Utility.getKeyHash(ContextUtil.context))

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                // 카카오톡으로 로그인
                UserApiClient.instance.loginWithKakaoTalk(ContextUtil.context) { token, error ->
                    if (error != null) {
                        Timber.e("로그인 실패")
                        Timber.e("error ${error.message}")

                    } else if (token != null) {
                        Timber.e("로그인 성공")
                        Timber.e("token $token")
                        getUserInfo()
                    }
                }
            }
        }
    }

    // token을 이용해 사용자 정보 가져오는 함수
    private fun getUserInfo() {
        // 사용자 정보 가져오기
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("TAG", "사용자 정보 요청 실패 $error")
            } else if (user != null) {
                Log.e("TAG", "사용자 정보 요청 성공 : $user")

                Timber.e("email ${user.kakaoAccount?.email}")
                Timber.e("name ${user.kakaoAccount?.profile?.nickname}")
                Timber.e("profile ${user.kakaoAccount?.profile?.profileImageUrl}")
                Timber.e("provider kakao")
                Timber.e("providerId ${user.id}")

                LoginModel.login.email = user.kakaoAccount?.email.toString()
                LoginModel.login.nickname = user.kakaoAccount?.profile?.nickname.toString()
                LoginModel.login.profile = user.kakaoAccount?.profile?.profileImageUrl.toString()
                LoginModel.login.provider = "kakao"
                LoginModel.login.providerId = user.id.toString()

                loginViewModel.loginUser()
            }
        }
    }
}