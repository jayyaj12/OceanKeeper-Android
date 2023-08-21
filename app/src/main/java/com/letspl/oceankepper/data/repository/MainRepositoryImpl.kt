package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.GetComingScheduleResponse
import com.letspl.oceankepper.data.dto.GetMyActivityDetailResponse
import com.letspl.oceankepper.data.dto.GetMyActivityResponse
import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.di.module.ApiModule
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
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