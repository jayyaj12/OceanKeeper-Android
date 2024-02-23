package com.letspl.oceankeeper.data.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class NaverLoginDto(
    val resultcode: String,
    val message: String,
    val response: GetNaverLoginResponseDto
)

@kotlinx.serialization.Serializable
data class GetNaverLoginResponseDto(
    val id: String,
    val nickname: String,
    @SerialName("profile_image")
    val profileImage: String,
    val email: String
)

