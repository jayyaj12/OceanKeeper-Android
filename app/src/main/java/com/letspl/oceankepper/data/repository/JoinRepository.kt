package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.JoinDto
import com.letspl.oceankepper.data.dto.JoinResponseDto
import com.letspl.oceankepper.data.dto.UploadProfileImageDto
import retrofit2.Response
import java.io.File

interface JoinRepository {

    suspend fun signUpUser(deviceToken: String, provider: String, providerId: String, nickname:String, email:String, profile: String): Response<JoinResponseDto>
    suspend fun uploadProfileImage(file: File): Response<UploadProfileImageDto>
}