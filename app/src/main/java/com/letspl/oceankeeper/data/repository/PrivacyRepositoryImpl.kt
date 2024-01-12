package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.PrivacyDto
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.network.ApiService
import com.letspl.oceankeeper.di.module.ApiModule
import retrofit2.Response
import javax.inject.Inject

class PrivacyRepositoryImpl @Inject constructor(@ApiModule.OceanService private val service: ApiService): PrivacyRepository {
    override suspend fun getPrivacyPolicy(): Response<PrivacyDto> {
        return service.getPrivacyPolicy("Bearer ${UserModel.userInfo.token.accessToken}")
    }
}