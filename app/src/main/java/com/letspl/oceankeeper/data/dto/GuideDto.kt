package com.letspl.oceankeeper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GetGuideDto(
    val response: GetGuideResponseDto
)
@Serializable
data class GetGuideResponseDto(
    val meta: GetGuideMetaDto,
    val guides: List<GetGuideListDto>
)
@Serializable
data class GetGuideMetaDto(
    val last: Boolean,
    val size: Int
)
@Serializable
data class GetGuideListDto(
    val createdAt: String,
    val id: Int,
    val modifiedAt: String,
    val title: String,
    val videoLink: String,
    val videoName: String,
)
