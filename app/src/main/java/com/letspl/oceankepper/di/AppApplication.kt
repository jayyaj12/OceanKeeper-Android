package com.letspl.oceankepper.di

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.letspl.oceankepper.util.ContextUtil
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AppApplication: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        Timber.plant(Timber.DebugTree())
    }
}