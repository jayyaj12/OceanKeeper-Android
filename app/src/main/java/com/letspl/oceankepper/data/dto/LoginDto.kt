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
    var deviceToken : String = "",
    var provider: String = "",
    var providerId: String = "",
    var nickname: String = "",
    var email: String = "",
    var profile: String = ""
)
