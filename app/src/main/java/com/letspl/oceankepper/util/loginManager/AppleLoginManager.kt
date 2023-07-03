package com.letspl.oceankepper.util.loginManager

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.letspl.oceankepper.ui.view.BaseActivity
import timber.log.Timber
import javax.inject.Inject

class AppleLoginManager (private val activity: Activity) {
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
                //이 객체로 후속 작업 진행

            }.addOnFailureListener { e ->
                Log.e("TTT", "checkPending:onFailure", e)
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
                Log.e("TTT", "activitySignIn:addOnSuccessListener")

            }.addOnFailureListener { e ->
                Log.e("TTT", "activitySignIn:onFailure", e)
            }
    }
}