package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.UploadProfileImageDto
import retrofit2.Response
import java.io.File

interface JoinRepository {

    suspend fun joinAccount(deviceToken: String, provider: String, providerId: String, nickname:String, email:String, profile: String)
    suspend fun uploadProfileImage(file: File): Response<UploadProfileImageDto>
}