package com.letspl.oceankepper.data.dto

@kotlinx.serialization.Serializable
data class NoteDto(
    val statusCode: Int,
    val timestamp: String,
    val response: NoteResponseDto
)

@kotlinx.serialization.Serializable
data class NoteResponseDto(
    val message: List<NoteItemDto>,
    val meta: NoteMetaDto
)

@kotlinx.serialization.Serializable
data class NoteItemDto(
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
data class NoteMetaDto(
    val last: Boolean,
    val size: Int
)