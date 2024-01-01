package com.letspl.oceankepper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val statusCode: Int,
    val timestamp: String,
    val response: NotificationResponseDto
)

@Serializable
data class NotificationResponseDto(
    val alarm: Boolean
)


@Serializable
data class NotificationListDto(
    val statusCode: Int,
    val timestamp: String,
    val response: NotificationListResponseDto
)
@Serializable
data class NotificationListResponseDto(
    val data: List<NotificationListDataDto>,
    val meta: NotificationListMetaDto
)
@Serializable
data class NotificationListDataDto(
    val contents: String,
    val createdAt: String,
    val id: Int,
    val read: Boolean
)

@Serializable
data class NotificationListMetaDto(
    val last: Boolean,
    val size: Int
)

data class NotificationItemDto(
    val id: Int,
    val contents: String,
    val createAt: String,
    val read: Boolean
)