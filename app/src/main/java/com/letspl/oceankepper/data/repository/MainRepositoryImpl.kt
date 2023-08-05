package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.GetComingScheduleResponse
import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.di.module.ApiModule
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(@ApiModule.OceanService private val service: ApiService): MainRepository {
    override suspend fun getComingSchedule(token: String, userId:String): Response<GetComingScheduleResponse> {
        return service.getComingSchedule(token, userId)
    }

}