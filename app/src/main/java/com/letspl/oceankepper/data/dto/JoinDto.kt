package com.letspl.oceankepper.data.dto

import retrofit2.http.Field

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
