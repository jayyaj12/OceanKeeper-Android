package com.letspl.oceankepper.data.dto

@kotlinx.serialization.Serializable
data class MessageDto(
    val statusCode: Int,
    val timestamp: String,
    val response: MessageResponseDto
)

@kotlinx.serialization.Serializable
data class MessageResponseDto(
    val messages: List<MessageItemDto>,
    val meta: MessageMetaDto
)

@kotlinx.serialization.Serializable
data class MessageItemDto(
    val activityId: String,
    val from: String,
    val garbageCategory: String,
    val id: String,
    val read: Boolean,
    val time: String,
    val title: String,
    val type: String
)

@kotlinx.serialization.Serializable
data class MessageMetaDto(
    val last: Boolean,
    val size: Int
)