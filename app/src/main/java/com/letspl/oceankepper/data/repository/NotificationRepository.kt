package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.LogoutDto
import com.letspl.oceankepper.data.dto.MessageDto
import com.letspl.oceankepper.data.dto.NotificationDto
import com.letspl.oceankepper.data.dto.PostSendMessageResultDto
import com.letspl.oceankepper.data.model.MessageModel
import retrofit2.Response

interface NotificationRepository {

    // 알림 설정
    suspend fun postNotificationAlarm(alarm: Boolean, userId: String): Response<NotificationDto>

    // 알림 설정 가져오기
    suspend fun getNotificationAlarm(userId: String): Response<NotificationDto>

}