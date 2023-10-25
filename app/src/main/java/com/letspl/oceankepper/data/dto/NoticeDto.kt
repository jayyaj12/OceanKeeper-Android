package com.letspl.oceankepper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetNoticeDto(
    val response: GetNoticeResponseDto
)
@Serializable
data class GetNoticeResponseDto(
    val meta: GetNoticeMetaDto,
    val notices: List<GetNoticeListDto>
)
@Serializable
data class GetNoticeMetaDto(
    val last: Boolean,
    val size: Int
)
@Serializable
data class GetNoticeListDto(
    val contents: String,
    val createdAt: String,
    val id: Int,
    val modifiedAt: String,
    val title: String
)

