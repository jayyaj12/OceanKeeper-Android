package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.ApplyApplicationDto
import com.letspl.oceankepper.data.dto.ComingScheduleItem
import com.letspl.oceankepper.data.dto.GetApplicationDetailDto
import com.letspl.oceankepper.data.dto.GetApplicationDetailResponse
import com.letspl.oceankepper.data.dto.GetComingScheduleResponse
import com.letspl.oceankepper.data.dto.GetMyActivityDetailResponse
import com.letspl.oceankepper.data.dto.GetMyActivityResponse
import com.letspl.oceankepper.data.dto.PatchApplicationDto
import com.letspl.oceankepper.data.dto.PostApplyApplicationBody
import retrofit2.Response

interface ApplyActivityRepository {
    // 활동 지원하기
    suspend fun postRecruitmentApplication(token: String, activity: PostApplyApplicationBody): Response<ApplyApplicationDto>
    // 특정 활동 지원서 보기
    suspend fun getDetailApplication(applicationId: String): Response<GetApplicationDetailDto>
    // 활동 지원서 수정
    suspend fun patchApplication(applicationId: String, patchApplicationBody: GetApplicationDetailResponse): Response<PatchApplicationDto>
}