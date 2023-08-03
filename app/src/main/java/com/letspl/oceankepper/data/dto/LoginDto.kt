package com.letspl.oceankepper.data.dto

@kotlinx.serialization.Serializable
data class LoginDto(
    val code: Int,
    val msg: String,
    val data: LoginDataDto
)

@kotlinx.serialization.Serializable
data class LoginDataDto(
    val id: Int,
    val nickname: String
)

// 프로필 이미지 업로드
@kotlinx.serialization.Serializable
data class UploadProfileImageDto(
    val url: String
)

@kotlinx.serialization.Serializable
data class LoginInfo(
    var deviceToken: String = "",
    var provider: String = "",
    var providerId: String = "",
    var nickname: String = "",
    var email: String = "",
    var profile: String = ""
)

// 로그인 요청 body
@kotlinx.serialization.Serializable
data class LoginBody(
    val deviceToken: String,
    val provider: String,
    val providerId: String
)


@kotlinx.serialization.Serializable
data class LoginResponseDto(
    val response: LoginUserResponseDto
)

@kotlinx.serialization.Serializable
data class LoginUserResponseDto(
    val token: LoginUserResponseTokenDto,
    val user: LoginUserResponseUserDto
)

@kotlinx.serialization.Serializable
data class LoginUserResponseTokenDto(
    var accessToken: String,
    var accessTokenExpiresIn: String,
    var grantType: String,
    var refreshToken: String
)

@kotlinx.serialization.Serializable
data class LoginUserResponseUserDto(
    var id: String,
    var nickname: String
)
