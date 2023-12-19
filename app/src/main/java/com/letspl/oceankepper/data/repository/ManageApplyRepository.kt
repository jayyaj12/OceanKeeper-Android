package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.model.ManageApplyMemberModel
import retrofit2.Response

interface ManageApplyRepository {
    // 크루원 불러오기
    suspend fun getCrewInfoList(activityId: String): Response<ManageApplyMemberModel.GetCrewInfoListResponseDto>
    // 크루원 정보 불러오기
    suspend fun getCrewDetail(applicationId: String): Response<ManageApplyMemberModel.GetCrewDetailResponseDto>
}