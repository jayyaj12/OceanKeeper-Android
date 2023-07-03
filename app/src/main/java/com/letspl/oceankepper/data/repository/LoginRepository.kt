package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.LoginDto
import com.letspl.oceankepper.data.dto.LoginUserResponseDto
import com.letspl.oceankepper.data.dto.NaverLoginDto
import retrofit2.Response

interface LoginRepository {

    // 네이버 로그인
    suspend fun getNaverUserInfo(token: String): Response<NaverLoginDto>

    // 계정 로그인
    suspend fun loginUser(deviceToken: String, provider: String, providerId: String): Response<LoginUserResponseDto>
}