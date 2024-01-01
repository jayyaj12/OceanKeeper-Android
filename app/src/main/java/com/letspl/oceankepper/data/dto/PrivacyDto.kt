package com.letspl.oceankepper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PrivacyDto(
    val statusCode: Int,
    val timestamp: String,
    val response: PrivacyResponseDto
)

@Serializable
data class PrivacyResponseDto(
    val contents: String,
    val createdAt: String,
    val id: Int,
)
