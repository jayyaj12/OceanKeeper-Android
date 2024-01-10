package com.letspl.oceankepper.util.loginManager

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.letspl.oceankepper.data.model.LoginModel
import com.letspl.oceankepper.ui.view.BaseActivity
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
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if(error != null) {
                loginViewModel.sendErrorMsg(error.message ?: "카카오 로그인에 실패하였습니다.")
            } else if(token != null) {
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e("TAG", "사용자 정보 요청 실패 $error")
                        loginViewModel.sendErrorMsg(error.message ?: "카카오 로그인에 실패하였습니다.")
                    } else if (user != null) {
                        Log.e("TAG", "사용자 정보 요청 성공 : $user")

                        Timber.e("email ${user.kakaoAccount?.email}")
                        Timber.e("name ${user.kakaoAccount?.profile?.nickname}")
                        Timber.e("profile ${user.kakaoAccount?.profile?.profileImageUrl}")
                        Timber.e("provider kakao")
                        Timber.e("providerId ${user.id}")

                        LoginModel.login.email = user.kakaoAccount?.email.toString()
                        LoginModel.login.nickname = user.kakaoAccount?.profile?.nickname.toString()
                        LoginModel.login.profile =
                            user.kakaoAccount?.profile?.profileImageUrl.toString()
                        LoginModel.login.provider = "kakao"
                        LoginModel.login.providerId = user.id.toString()

                        loginViewModel.loginUser()
                    }
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(ContextUtil.context)) {
            // 카카오톡으로 로그인
            UserApiClient.instance.loginWithKakaoTalk(ContextUtil.context) { token, error ->
                if (error != null) {
                    Timber.e("카카오톡 로그인 실패")

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Timber.e("사용자 뒤로가기")
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(ContextUtil.context, callback = callback)
                    Timber.e("카카오 로그인 성공")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(ContextUtil.context, callback = callback)
        }
    }

    // token을 이용해 사용자 정보 가져오는 함수
    private fun getUserInfo() {
        // 사용자 정보 가져오기

    }
}