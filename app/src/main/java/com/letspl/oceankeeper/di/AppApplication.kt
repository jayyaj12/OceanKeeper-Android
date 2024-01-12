package com.letspl.oceankeeper.di

import android.app.Activity
import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
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
        KakaoSdk.init(this, com.letspl.oceankeeper.BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}