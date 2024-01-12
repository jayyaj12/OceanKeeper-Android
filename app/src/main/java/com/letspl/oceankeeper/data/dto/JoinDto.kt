package com.letspl.oceankeeper.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class JoinResponseDto(
    val statusCode: Int,
    val timestamp: String,
    val response: JoinDto
)
@kotlinx.serialization.Serializable
data class JoinDto(
    val id: String,
    val nickname: String
)

@kotlinx.serialization.Serializable
data class SignUpBody(
    val deviceToken: String,
    val email: String,
    val nickname: String,
    val profile: String,
    val provider: String,
    val providerId: String
)
