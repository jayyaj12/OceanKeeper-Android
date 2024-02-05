package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.*
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.network.ApiService
import com.letspl.oceankeeper.di.module.ApiModule
import retrofit2.Response
import javax.inject.Inject

class ApplyActivityRepositoryImpl @Inject constructor(@ApiModule.OceanRetrofit private val apiService: ApiService): ApplyActivityRepository {

    override suspend fun postRecruitmentApplication(
        token: String,
        activity: PostApplyApplicationBody
    ): Response<ApplyApplicationDto> {
       return apiService.postRecruitmentApplication(token, activity)
    }

    override suspend fun getLastRecruitmentApplication(): Response<GetLastRecruitmentApplicationDto> {
        return apiService.getLastRecruitmentApplication("Bearer ${UserModel.userInfo.token.accessToken}")
    }

    override suspend fun getDetailApplication(applicationId: String): Response<GetApplicationDetailDto> {
        return apiService.getDetailApplication("Bearer ${UserModel.userInfo.token.accessToken}", applicationId)
    }

    override suspend fun patchApplication(
        applicationId: String,
        patchApplicationBody: PatchApplyApplicationBody
    ): Response<PatchApplicationDto> {
        return apiService.patchApplication("Bearer ${UserModel.userInfo.token.accessToken}", applicationId, patchApplicationBody)
    }

    override suspend fun getPrivacyPolicy(): Response<PrivacyDto> {
        return apiService.getPrivacyPolicy("Bearer ${UserModel.userInfo.token.accessToken}")
    }
}