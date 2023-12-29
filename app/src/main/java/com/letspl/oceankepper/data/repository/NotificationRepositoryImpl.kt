package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.LogoutDto
import com.letspl.oceankepper.data.dto.MessageDto
import com.letspl.oceankepper.data.dto.NotificationDto
import com.letspl.oceankepper.data.dto.PostSendMessageResultDto
import com.letspl.oceankepper.data.model.MessageModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.di.module.ApiModule
import retrofit2.Response
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(@ApiModule.OceanService private val service: ApiService): NotificationRepository {

    override suspend fun postNotificationAlarm(
        alarm: Boolean,
        userId: String
    ): Response<NotificationDto> {
        return service.postNotificationAlarm("Bearer ${UserModel.userInfo.token.accessToken}", alarm, userId)
    }
}