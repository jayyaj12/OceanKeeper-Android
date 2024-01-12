package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.*
import retrofit2.Response

interface ApplyActivityRepository {
    // 활동 지원하기
    suspend fun postRecruitmentApplication(token: String, activity: PostApplyApplicationBody): Response<ApplyApplicationDto>
    // 특정 활동 지원서 보기
    suspend fun getDetailApplication(applicationId: String): Response<GetApplicationDetailDto>
    // 활동 지원서 수정
    suspend fun patchApplication(applicationId: String, patchApplicationBody: PatchApplyApplicationBody): Response<PatchApplicationDto>
}