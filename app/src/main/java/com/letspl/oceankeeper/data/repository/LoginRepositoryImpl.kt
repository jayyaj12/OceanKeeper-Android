package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.LoginBody
import com.letspl.oceankeeper.data.dto.LoginResponseDto
import com.letspl.oceankeeper.data.dto.LogoutBody
import com.letspl.oceankeeper.data.dto.LogoutDto
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.network.ApiService
import com.letspl.oceankeeper.di.module.ApiModule
import retrofit2.Response
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(@ApiModule.NaverRetrofit private val naverApiService: ApiService, @ApiModule.OceanRetrofit private val oceanApiService: ApiService) :
    LoginRepository {

    // 네이버 계정 정보 조회
    override suspend fun getNaverUserInfo(token: String) = naverApiService.getNaverUserInfo(token)

    // 로그인
    override suspend fun loginUser(
        deviceToken: String,
        provider: String,
        providerId: String
    ): Response<LoginResponseDto> {
        return oceanApiService.loginUser(LoginBody(
            deviceToken, provider, providerId
        ))
    }

    // 로그아웃 
    override suspend fun logoutUser(
        deviceToken: String,
        provider: String,
        providerId: String
    ): Response<LogoutDto> {
        return oceanApiService.postLogout("Bearer ${UserModel.userInfo.token.accessToken}", LogoutBody(deviceToken, provider, providerId))
    }

    // 탈퇴하기
    override suspend fun withdrawAccount(
        deviceToken: String,
        provider: String,
        providerId: String
    ): Response<LogoutDto> {
        return oceanApiService.postWithdraw("Bearer ${UserModel.userInfo.token.accessToken}", LogoutBody(deviceToken, provider, providerId))
    }
}