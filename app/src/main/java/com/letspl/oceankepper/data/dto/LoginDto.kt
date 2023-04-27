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
