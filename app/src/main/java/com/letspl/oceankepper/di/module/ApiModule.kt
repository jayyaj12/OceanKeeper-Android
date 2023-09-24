package com.letspl.oceankepper.di.module

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.letspl.oceankepper.BuildConfig
import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.data.repository.ActivityRepositoryImpl
import com.letspl.oceankepper.data.repository.ApplyActivityRepositoryImpl
import com.letspl.oceankepper.data.repository.JoinRepositoryImpl
import com.letspl.oceankepper.data.repository.LoginRepositoryImpl
import com.letspl.oceankepper.data.repository.MainRepositoryImpl
import com.letspl.oceankepper.data.repository.MessageRepositoryImpl
import com.letspl.oceankepper.ui.dialog.ProgressDialog
import com.letspl.oceankepper.ui.view.BaseActivity
import com.letspl.oceankepper.ui.viewmodel.ActivityRecruit2ViewModel
import com.letspl.oceankepper.ui.viewmodel.ActivityRecruitViewModel
import com.letspl.oceankepper.ui.viewmodel.JoinViewModel
import com.letspl.oceankepper.ui.viewmodel.LoginViewModel
import com.letspl.oceankepper.ui.viewmodel.MessageViewModel
import com.letspl.oceankepper.util.ContextUtil
import com.letspl.oceankepper.util.loginManager.KakaoLoginManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NaverRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KaKaoRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OceanRetrofit

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NaverService

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class KaKaoService

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class GoogleService

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OceanService

    @Singleton
    @Provides
    fun provideOkHttpClient() = run {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    @NaverRetrofit
    fun provideNaverRetrofit(okHttpClient: OkHttpClient) = run {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.NAVER_BASE_URL)
            .addConverterFactory(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true // 지정되지 않은 key 값은 무시
                    coerceInputValues = true // default 값 설정
                    explicitNulls = false // 없는 필드는 null로 설정
                }.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    @Singleton
    @Provides
    @KaKaoRetrofit
    fun provideKaKaoRetrofit(okHttpClient: OkHttpClient) = run {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.NAVER_BASE_URL)
            .addConverterFactory(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true // 지정되지 않은 key 값은 무시
                    coerceInputValues = true // default 값 설정
                    explicitNulls = false // 없는 필드는 null로 설정
                }.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    @Singleton
    @Provides
    @GoogleRetrofit
    fun provideGoogleRetrofit(okHttpClient: OkHttpClient) = run {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.NAVER_BASE_URL)
            .addConverterFactory(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true // 지정되지 않은 key 값은 무시
                    coerceInputValues = true // default 값 설정
                    explicitNulls = false // 없는 필드는 null로 설정
                }.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }


    @Singleton
    @Provides
    @OceanRetrofit
    fun provideOceanRetrofit(okHttpClient: OkHttpClient) = run {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.SERVER_BASE_URL)
            .addConverterFactory(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true // 지정되지 않은 key 값은 무시
                    coerceInputValues = true // default 값 설정
                    explicitNulls = false // 없는 필드는 null로 설정
                }.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    @Singleton
    @Provides
    @NaverService
    fun provideNaverApiService(@NaverRetrofit retrofit: Retrofit) = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    @KaKaoService
    fun provideKaKaoApiService(@KaKaoRetrofit retrofit: Retrofit) = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    @GoogleService
    fun provideGoogleApiService(@GoogleRetrofit retrofit: Retrofit) = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    @OceanService
    fun provideOceanApiService(@OceanRetrofit retrofit: Retrofit) = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideLoginRepositoryImpl(@NaverService apiService: ApiService, @OceanService oceanService: ApiService) = LoginRepositoryImpl(apiService, oceanService)

    @Singleton
    @Provides
    fun provideJoinRepositoryImpl(@OceanService apiService: ApiService) = JoinRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideMainRepositoryImpl(@OceanService apiService: ApiService) = MainRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideApplyActivityRepositoryImpl(@OceanService apiService: ApiService) = ApplyActivityRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideMessageRepositoryImpl(@OceanService apiService: ApiService) = MessageRepositoryImpl(apiService)

    @Singleton
    @Provides
    fun provideLoginViewModel(@NaverService apiService: ApiService, @OceanService oceanService: ApiService) = LoginViewModel(LoginRepositoryImpl(apiService, oceanService))
    @Singleton
    @Provides
    fun provideMessageViewModel(@NaverService apiService: ApiService, @OceanService oceanService: ApiService) = MessageViewModel(MessageRepositoryImpl(oceanService))

    @Singleton
    @Provides
    fun provideActivityRecruit2ViewModelViewModel(@OceanService apiService: ApiService) = ActivityRecruit2ViewModel(ActivityRecruitViewModel(), ActivityRepositoryImpl(apiService))

    @Singleton
    @Provides
    fun provideBaseActivity() = BaseActivity()

    @Singleton
    @Provides
    fun provideKakaoLoginManager(loginViewModel: LoginViewModel) = KakaoLoginManager(loginViewModel)

    @Singleton
    @Provides
    fun provideProgressDialog() = ProgressDialog(ContextUtil.context)

}