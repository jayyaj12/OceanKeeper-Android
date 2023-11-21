package com.letspl.oceankepper.data.dto

@kotlinx.serialization.Serializable
data class GetCheckDuplicateNicknameResponse(
    val response: String,
    val statusCode: Int,
    val timestamp: String
)

@kotlinx.serialization.Serializable
data class PutNicknameBody(
    val nickname: String,
    val userId: String
)

@kotlinx.serialization.Serializable
data class PutNicknameResponse(
    val response: String,
    val statusCode: Int,
    val timestamp: String
)
