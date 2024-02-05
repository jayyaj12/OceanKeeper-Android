package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.NotificationDto
import com.letspl.oceankeeper.data.dto.NotificationListDto
import com.letspl.oceankeeper.data.dto.PrivacyDto
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.network.ApiService
import com.letspl.oceankeeper.di.module.ApiModule
import retrofit2.Response
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(@ApiModule.OceanService private val service: ApiService): NotificationRepository {

    override suspend fun postNotificationAlarm(
        alarm: Boolean,
        userId: String
    ): Response<NotificationDto> {
        return service.postNotificationAlarm("Bearer ${UserModel.userInfo.token.accessToken}", alarm, userId)
    }

    override suspend fun getNotificationAlarm(userId: String): Response<NotificationDto> {
        return service.getNotificationAlarm("Bearer ${UserModel.userInfo.token.accessToken}", userId)
    }

    override suspend fun getNotificationList(
        size: Int,
        id: Int?,
        userId: String
    ): Response<NotificationListDto> {
        return service.getNotificationList("Bearer ${UserModel.userInfo.token.accessToken}", userId, null, id)
    }

    override suspend fun getTerms(): Response<PrivacyDto> {
        return service.getTerms("Bearer ${UserModel.userInfo.token.accessToken}")
    }
}