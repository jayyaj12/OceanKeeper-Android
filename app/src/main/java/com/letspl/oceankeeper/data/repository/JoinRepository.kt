package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.JoinResponseDto
import com.letspl.oceankeeper.data.dto.UploadProfileImageDto
import retrofit2.Response
import java.io.File

interface JoinRepository {

    suspend fun signUpUser(deviceToken: String, provider: String, providerId: String, nickname:String, email:String, profile: String): Response<JoinResponseDto>
    suspend fun uploadProfileImage(file: File): Response<UploadProfileImageDto>
}