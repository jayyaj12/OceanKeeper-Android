package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.GetComingScheduleResponse
import com.letspl.oceankeeper.data.dto.GetMyActivityDetailResponse
import com.letspl.oceankeeper.data.dto.GetMyActivityResponse
import com.letspl.oceankeeper.data.network.ApiService
import com.letspl.oceankeeper.di.module.ApiModule
import retrofit2.Response
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(@ApiModule.OceanService private val service: ApiService): MainRepository {
    override suspend fun getComingSchedule(token: String, userId:String): Response<GetComingScheduleResponse> {
        return service.getComingSchedule(token, userId)
    }

    override suspend fun getMyActivity(
        token: String,
        activityId: String?,
        garbageCategory: String?,
        locationTag: String?,
        size: Int,
        status: String?
    ): Response<GetMyActivityResponse> {
        return service.getMyActivity(token, activityId, garbageCategory, locationTag, size, status)
    }

    override suspend fun getMyActivityDetail(
        token: String,
        activityId: String
    ): Response<GetMyActivityDetailResponse> {
        return service.getMyActivityDetail(token, activityId)
    }
}