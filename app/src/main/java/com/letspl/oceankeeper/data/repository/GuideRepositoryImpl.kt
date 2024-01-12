package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.GetGuideDto
import com.letspl.oceankeeper.data.network.ApiService
import com.letspl.oceankeeper.di.module.ApiModule
import retrofit2.Response
import javax.inject.Inject

class GuideRepositoryImpl @Inject constructor(@ApiModule.OceanRetrofit private val oceanApiService: ApiService) :
    GuideRepository {

    override suspend fun getGuide(token: String, guideId: Int?, size: Int): Response<GetGuideDto> {
        return oceanApiService.getGuide(token, guideId, size)
    }
}