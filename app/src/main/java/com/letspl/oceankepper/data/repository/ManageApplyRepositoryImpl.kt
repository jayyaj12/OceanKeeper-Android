package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.model.ManageApplyMemberModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.di.module.ApiModule
import retrofit2.Response

class ManageApplyRepositoryImpl(@ApiModule.OceanRetrofit private val apiService: ApiService): ManageApplyRepository {
    override suspend fun getCrewInfoList(activityId: String): Response<ManageApplyMemberModel.GetCrewInfoListResponseDto> {
        return apiService.getCrewInfoList("Bearer ${UserModel.userInfo.token.accessToken}", activityId)
    }

    override suspend fun getCrewDetail(applicationId: String): Response<ManageApplyMemberModel.GetCrewDetailResponseDto> {
        return apiService.getCrewDetail("Bearer ${UserModel.userInfo.token.accessToken}", applicationId)
    }
}