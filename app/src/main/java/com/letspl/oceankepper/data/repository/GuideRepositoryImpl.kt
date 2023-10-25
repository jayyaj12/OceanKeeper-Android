package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.GetGuideDto
import com.letspl.oceankepper.data.dto.GetNoticeDto
import com.letspl.oceankepper.data.dto.LoginBody
import com.letspl.oceankepper.data.dto.LoginResponseDto
import com.letspl.oceankepper.data.dto.LoginUserResponseDto
import com.letspl.oceankepper.data.dto.NaverLoginDto
import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.di.module.ApiModule
import retrofit2.Response
import javax.inject.Inject

class GuideRepositoryImpl @Inject constructor(@ApiModule.OceanRetrofit private val oceanApiService: ApiService) :
    GuideRepository {

    override suspend fun getGuide(token: String, guideId: Int?, size: Int): Response<GetGuideDto> {
        return oceanApiService.getGuide(token, guideId, size)
    }
}