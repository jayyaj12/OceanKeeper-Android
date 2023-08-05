package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.ComingScheduleItem
import com.letspl.oceankepper.data.dto.GetComingScheduleResponse
import retrofit2.Response

interface MainRepository {
    suspend fun getComingSchedule(token: String, userId: String): Response<GetComingScheduleResponse>
}