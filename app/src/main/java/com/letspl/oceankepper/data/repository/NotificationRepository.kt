package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.LogoutDto
import com.letspl.oceankepper.data.dto.MessageDto
import com.letspl.oceankepper.data.dto.NotificationDto
import com.letspl.oceankepper.data.dto.PostSendMessageResultDto
import com.letspl.oceankepper.data.model.MessageModel
import retrofit2.Response

interface NotificationRepository {

    suspend fun postNotificationAlarm(alarm: Boolean, userId: String): Response<NotificationDto>

}