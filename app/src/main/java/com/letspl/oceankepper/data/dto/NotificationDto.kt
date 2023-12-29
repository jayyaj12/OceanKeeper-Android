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