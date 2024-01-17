package com.letspl.oceankeeper.util.loginManager

import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.letspl.oceankeeper.data.model.LoginModel
import com.letspl.oceankeeper.ui.viewmodel.LoginViewModel
import com.letspl.oceankeeper.util.ContextUtil
import timber.log.Timber
import javax.inject.Inject


class KakaoLoginManager @Inject constructor(private val loginViewModel: LoginViewModel) {
    // 로그인 공통 callback 구성

    fun onClickedKakaoLogin() {
        Timber.e(Utility.getKeyHash(ContextUtil.context))
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            Timber.e("callback error1 $error")
            if(error != null) {
                loginViewModel.sendErrorMsg(error.message ?: "카카오 로그인에 실패하였습니다.")
            } else if(token != null) {
                Timber.e("callback token $token")
                UserApiClient.instance.me { user, error ->
                    Timber.e("callback error2 $error")
                    Timber.e("callback user $user")
                    if (error != null) {
                        loginViewModel.sendErrorMsg(error.message ?: "카카오 로그인에 실패하였습니다.")
                    } else if (user != null) {
                        LoginModel.login.email = user.kakaoAccount?.email.toString()
                        LoginModel.login.nickname = user.kakaoAccount?.profile?.nickname.toString()
                        LoginModel.login.profile =
                            user.kakaoAccount?.profile?.profileImageUrl.toString()
                        LoginModel.login.provider = "kakao"
                        LoginModel.login.providerId = user.id.toString()

                        loginViewModel.loginUser("KAKAO")
                    }
                }
            }
        }

        Timber.e("UserApiClient.instance.isKakaoTalkLoginAvailable(ContextUtil.context) ${UserApiClient.instance.isKakaoTalkLoginAvailable(ContextUtil.context)}")
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(ContextUtil.context)) {
            // 카카오톡으로 로그인
            UserApiClient.instance.loginWithKakaoTalk(ContextUtil.context) { token, error ->
                Timber.e("loginWithKakaoTalk error $error")

                if (error != null) {
                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Timber.e("error is ClientError error $error")
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(ContextUtil.context, callback = callback)
                } else {
                    UserApiClient.instance.me { user, error ->
                        Timber.e("callback error2 $error")
                        Timber.e("callback user $user")
                        if (error != null) {
                            loginViewModel.sendErrorMsg(error.message ?: "카카오 로그인에 실패하였습니다.")
                        } else if (user != null) {
                            LoginModel.login.email = user.kakaoAccount?.email.toString()
                            LoginModel.login.nickname = user.kakaoAccount?.profile?.nickname.toString()
                            LoginModel.login.profile =
                                user.kakaoAccount?.profile?.profileImageUrl.toString()
                            LoginModel.login.provider = "kakao"
                            LoginModel.login.providerId = user.id.toString()

                            loginViewModel.loginUser("KAKAO")
                        }
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(ContextUtil.context, callback = callback)
        }
    }

    fun startLogout(): Boolean {
        var logoutResult = false
        UserApiClient.instance.logout { error ->
            logoutResult = error == null
        }
        return logoutResult
    }
}