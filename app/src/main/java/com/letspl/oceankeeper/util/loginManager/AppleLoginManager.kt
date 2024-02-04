package com.letspl.oceankeeper.util.loginManager

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.letspl.oceankeeper.data.model.LoginModel
import com.letspl.oceankeeper.ui.viewmodel.LoginViewModel
import timber.log.Timber
import javax.inject.Inject

class AppleLoginManager @Inject constructor (private val activity: Activity, private val loginViewModel: LoginViewModel) {
    private lateinit var mProvider: OAuthProvider.Builder
    private lateinit var mAuth: FirebaseAuth

    init {
        initAuth()
    }

    // 1. 인증 API 초기화
    fun initAuth() {
        mProvider = OAuthProvider.newBuilder("apple.com")
        mProvider.scopes = listOf("email", "name") //로그인 후 받고 싶은 유저 정보 범위
        mProvider.addCustomParameter("locale", "ko")

        mAuth = FirebaseAuth.getInstance()
    }

    // 2. 이미 받은 응답이 있는지 확인
    fun checkPending(){

        val pending = mAuth.pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener { authResult ->
                Log.e("TTT", "checkPending:onSuccess:$authResult")
                //로그인 결과 및 유저 정보가 AuthResult 객체에 담겨서 받아짐
                Timber.e("authResult.user.email ${authResult.user?.email}")
                Timber.e("authResult.user.providerData ${authResult.user?.providerData}")
                Timber.e("authResult.user.providerId ${authResult.user?.providerId}")
                Timber.e("authResult.user.displayName ${authResult.user?.displayName}")
                Timber.e("authResult.user.uid ${authResult.user?.uid}")
                Timber.e("authResult.user ${authResult.user}")
                //이 객체로 후속 작업 진행

                appleLogin(authResult)
            }.addOnFailureListener { e ->
                Log.e("TTT", "checkPending:onFailure", e)
                loginViewModel.sendErrorMsg(e.message ?: "")
            }
        } else {
            startAuth()
        }
    }

    // 3. 이미 받은 응답이 없다면 로그인 절차 시작
    private fun startAuth(){
        Timber.e("baseActivity $activity")
        mAuth.startActivityForSignInWithProvider(activity, mProvider.build())
            .addOnSuccessListener { authResult ->
                //로그인 결과 및 유저 정보가 AuthResult 객체에 담겨서 받아짐
                //이 객체로 후속 작업 진행
                Log.e("TTT", "checkPending:onSuccess:$authResult")
                //로그인 결과 및 유저 정보가 AuthResult 객체에 담겨서 받아짐
                Timber.e("authResult.user.email ${authResult.user?.email}")
                Timber.e("authResult.user.providerData ${authResult.user?.providerData}")
                Timber.e("authResult.user.providerId ${authResult.user?.providerId}")
                Timber.e("authResult.user.displayName ${authResult.user?.displayName}")
                Timber.e("authResult.user.uid ${authResult.user?.uid}")
                Timber.e("authResult.user.photoUrl ${authResult.user?.photoUrl}")
                Timber.e("authResult.user ${authResult.user}")

                appleLogin(authResult)


            }.addOnFailureListener { e ->
                Log.e("TTT", "activitySignIn:onFailure", e)
                Timber.e("asdasd ${e.message}")
                if(e.message == "An internal error has occurred.") {
                    loginViewModel.sendErrorMsg("네트워크에 연결되어 있지 않습니다.\n네트워크 연결 후 다시 시도해주세요." ?: "")
                } else {
                    loginViewModel.sendErrorMsg(e.message ?: "")
                }
            }
    }

    private fun appleLogin(authResult: AuthResult) {
        authResult.user?.let {
            LoginModel.login.email = it.email.toString()
            LoginModel.login.nickname = it.displayName.toString()
            LoginModel.login.profile = ""
            LoginModel.login.provider = "apple"
            LoginModel.login.providerId = it.uid
        }

        loginViewModel.loginUser("APPLE")
    }
}