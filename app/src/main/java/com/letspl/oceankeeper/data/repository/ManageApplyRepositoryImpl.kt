package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.model.ManageApplyMemberModel
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.network.ApiService
import com.letspl.oceankeeper.di.module.ApiModule
import okhttp3.ResponseBody
import retrofit2.Response

class ManageApplyRepositoryImpl(@ApiModule.OceanRetrofit private val apiService: ApiService): ManageApplyRepository {
    override suspend fun getCrewInfoList(activityId: String): Response<ManageApplyMemberModel.GetCrewInfoListResponseDto> {
        return apiService.getCrewInfoList("Bearer ${UserModel.userInfo.token.accessToken}", activityId)
    }

    override suspend fun getCrewDetail(applicationId: String): Response<ManageApplyMemberModel.GetCrewDetailResponseDto> {
        return apiService.getCrewDetail("Bearer ${UserModel.userInfo.token.accessToken}", applicationId)
    }

    override suspend fun postCrewStatus(body: ManageApplyMemberModel.PostCrewStatusBody): Response<ManageApplyMemberModel.PostCrewStatusDto> {
        return apiService.postCrewStatus("Bearer ${UserModel.userInfo.token.accessToken}", body)
    }

    override suspend fun getCrewInfoFileDownloadUrl(activityId: String): Response<ResponseBody> {
        return apiService.getCrewInfoFile("Bearer ${UserModel.userInfo.token.accessToken}", activityId)
    }
}