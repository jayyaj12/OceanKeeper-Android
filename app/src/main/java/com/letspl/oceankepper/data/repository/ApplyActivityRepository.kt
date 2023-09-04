package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.ApplyApplicationDto
import com.letspl.oceankepper.data.dto.ComingScheduleItem
import com.letspl.oceankepper.data.dto.GetComingScheduleResponse
import com.letspl.oceankepper.data.dto.GetMyActivityDetailResponse
import com.letspl.oceankepper.data.dto.GetMyActivityResponse
import com.letspl.oceankepper.data.dto.PostApplyApplicationBody
import retrofit2.Response

interface ApplyActivityRepository {
    suspend fun postRecruitmentApplication(token: String, activity: PostApplyApplicationBody): Response<ApplyApplicationDto>
}