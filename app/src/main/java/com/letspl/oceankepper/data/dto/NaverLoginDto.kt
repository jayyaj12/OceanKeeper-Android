package com.letspl.oceankepper.data.dto

import android.os.Parcelable

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
    val profile_image: String,
    val email: String,
    val name: String,
)

