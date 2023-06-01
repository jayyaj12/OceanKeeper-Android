package com.letspl.oceankepper.data.dto

data class LoginDto(
    val code: Int,
    val msg: String,
    val data: LoginDataDto
)

data class LoginDataDto(
    val id: Int,
    val nickname: String
)

// 프로필 이미지 업로드
data class UploadProfileImageDto(
    val url: String
)

data class LoginInfo(
    var deviceToken : String = "",
    var provider: String = "",
    var providerId: String = "",
    var nickname: String = "",
    var email: String = "",
    var profile: String = ""
)
