package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.model.ManageApplyMemberModel
import retrofit2.Response

interface ManageApplyRepository {
    suspend fun getCrewInfoList(activityId: String): Response<ManageApplyMemberModel.GetCrewInfoListResponseDto>
}