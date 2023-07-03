package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.LoginBody
import com.letspl.oceankepper.data.dto.LoginUserResponseDto
import com.letspl.oceankepper.data.dto.NaverLoginDto
import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.di.module.ApiModule
import retrofit2.Response
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(@ApiModule.NaverRetrofit private val naverApiService: ApiService, @ApiModule.OceanRetrofit private val oceanApiService: ApiService) :
    LoginRepository {

    // 네이버 계정 정보 조회
    override suspend fun getNaverUserInfo(token: String) = naverApiService.getNaverUserInfo(token)

    override suspend fun loginUser(
        deviceToken: String,
        provider: String,
        providerId: String
    ): Response<LoginUserResponseDto> {
        return oceanApiService.loginUser(LoginBody(
            deviceToken, provider, providerId
        ))
    }
}