package com.letspl.oceankepper.data.dto

import android.os.Parcelable
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
    val email: String,
    val name: String,
)

