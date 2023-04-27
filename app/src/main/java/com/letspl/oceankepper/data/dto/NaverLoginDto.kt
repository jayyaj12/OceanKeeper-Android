package com.letspl.oceankepper.data.dto

data class NaverLoginDto(
    val resultCode: String,
    val message: String,
    val response: GetNaverLoginResponseDto
)

data class GetNaverLoginResponseDto(
    val id: String,
    val nickname: String,
    val name: String,
    val email: String,
    val gender: String,
    val age: String,
    val birthday: String,
    val profile_image: String,
    val birthyear: String,
    val mobile: String
)

