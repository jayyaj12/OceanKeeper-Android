package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.NotificationDto
import com.letspl.oceankeeper.data.dto.NotificationListDto
import retrofit2.Response

interface NotificationRepository {

    // 알림 설정
    suspend fun postNotificationAlarm(alarm: Boolean, userId: String): Response<NotificationDto>

    // 알림 설정 가져오기
    suspend fun getNotificationAlarm(userId: String): Response<NotificationDto>

    // 알림 리스트 가져오기
    suspend fun getNotificationList(size:Int, id: Int?, userId: String): Response<NotificationListDto>

}