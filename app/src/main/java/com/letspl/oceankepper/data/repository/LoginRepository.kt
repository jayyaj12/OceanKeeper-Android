package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.LoginDto
import com.letspl.oceankepper.data.dto.NaverLoginDto
import retrofit2.Response

interface LoginRepository {

    // 앱 로그인
    suspend fun postAppLogin(provider: String, providerId: String): Response<LoginDto>
    // 네이버 로그인
    suspend fun getNaverUserInfo(token: String): Response<NaverLoginDto>
}