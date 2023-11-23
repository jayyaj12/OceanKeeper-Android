package com.letspl.oceankepper.util.loginManager

import android.util.Log
import android.widget.Toast
import com.letspl.oceankepper.BuildConfig
import com.letspl.oceankepper.ui.viewmodel.LoginViewModel
import com.letspl.oceankepper.util.BaseUrlType
import com.letspl.oceankepper.util.ContextUtil
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


class NaverLoginManager @Inject constructor(private val loginViewModel: LoginViewModel) {
    // NaverLoginSdk 초기화

    init {
        setUpNaverLogin()
    }

    // naverLoginSdk initialize
    fun setUpNaverLogin() {
        NaverIdLoginSDK.initialize(
            ContextUtil.context,
            BuildConfig.NAVER_CLIENT_ID,
            BuildConfig.NAVER_SECRET_CLIEND_ID, "Ocean Keeper App"
        )
    }

    // 로그인
    fun startNaverLogin() {
        var naverToken: String? = ""

        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                val userId = response.profile?.id
                // 토큰으로 사용자 정보 조회
                naverToken?.let {
                    // 로그인 토큰을 가지고 실제 데이터 조회
                    loginViewModel.getNaverUserInfo(it)
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        /** OAuthLoginCallback을 authenticate() 메서드 호출 시 파라미터로 전달하거나 NidOAuthLoginButton 객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다. */
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                naverToken = NaverIdLoginSDK.getAccessToken()

                //로그인 유저 정보 가져오기
                NidOAuthLogin().callProfileApi(profileCallback)
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Timber.e("errorCode: ${errorCode}")
                Timber.e("errorDescription: ${errorDescription}")
                Toast.makeText(
                    ContextUtil.context, "errorCode: ${errorCode}\n" +
                            "errorDescription: ${errorDescription}", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        NaverIdLoginSDK.authenticate(ContextUtil.context, oauthLoginCallback)
    }

    // 로그아웃
    fun startNaverLogout() {
        NaverIdLoginSDK.logout()
        Toast.makeText(ContextUtil.context, "네이버 아이디 로그아웃 성공!", Toast.LENGTH_SHORT).show()
    }

    // 연동 해제
    fun startNaverDeleteToken() {
        NidOAuthLogin().callDeleteTokenApi(ContextUtil.context, object : OAuthLoginCallback {
            override fun onSuccess() {
                //서버에서 토큰 삭제에 성공한 상태
                Toast.makeText(ContextUtil.context, "네이버 아이디 토큰삭제 성공!", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                Log.d("naver", "errorCode: ${NaverIdLoginSDK.getLastErrorCode().code}")
                Log.d("naver", "errorDesc: ${NaverIdLoginSDK.getLastErrorDescription()}")
            }

            override fun onError(errorCode: Int, message: String) {
                // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                onFailure(errorCode, message)
            }
        })
    }
}