package com.letspl.oceankepper.di

import android.app.Activity
import android.app.Application
import android.content.Context
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import com.letspl.oceankepper.util.ContextUtil
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AppApplication: Application() {
    companion object {
        lateinit var appContext: Context
        lateinit var activity: Activity
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        KakaoSdk.init(this, com.letspl.oceankepper.BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}