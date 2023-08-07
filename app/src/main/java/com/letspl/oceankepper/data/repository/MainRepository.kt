package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.ComingScheduleItem
import com.letspl.oceankepper.data.dto.GetComingScheduleResponse
import com.letspl.oceankepper.data.dto.GetMyActivityResponse
import retrofit2.Response

interface MainRepository {
    // 다가오는 일정 조회
    suspend fun getComingSchedule(token: String, userId: String): Response<GetComingScheduleResponse>
    // 활동 조회 처음 조회시에는 activityId 안보냄
    suspend fun getMyActivity(token: String, garbageCategory: String, locationTag: String, size: Int): Response<GetMyActivityResponse>
    // 활동 조회
    suspend fun getMyActivity(token: String, activityId: String, garbageCategory: String, locationTag: String, size: Int): Response<GetMyActivityResponse>
    // 활동 조회 활동 상태 선택 했을 경우
    suspend fun getMyActivity(token: String, activityId: String, garbageCategory: String, locationTag: String, size: Int, status: String): Response<GetMyActivityResponse>
}