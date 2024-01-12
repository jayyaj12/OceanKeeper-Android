package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.LoginResponseDto
import com.letspl.oceankeeper.data.dto.LogoutDto
import com.letspl.oceankeeper.data.dto.NaverLoginDto
import retrofit2.Response

interface LoginRepository {

    // 네이버 로그인
    suspend fun getNaverUserInfo(token: String): Response<NaverLoginDto>

    // 계정 로그인
    suspend fun loginUser(deviceToken: String, provider: String, providerId: String): Response<LoginResponseDto>

    // 계정 로그아웃
    suspend fun logoutUser(deviceToken: String, provider: String, providerId: String): Response<LogoutDto>

    // 계정 로그아웃
    suspend fun withdrawAccount(deviceToken: String, provider: String, providerId: String): Response<LogoutDto>
}