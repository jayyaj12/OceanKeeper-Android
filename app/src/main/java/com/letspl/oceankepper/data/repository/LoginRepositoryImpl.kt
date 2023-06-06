package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.di.module.ApiModule
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(@ApiModule.NaverRetrofit private val apiService: ApiService) :
    LoginRepository {

    // 네이버 계정 정보 조회
    override suspend fun getNaverUserInfo(token: String) = apiService.getNaverUserInfo(token)
}