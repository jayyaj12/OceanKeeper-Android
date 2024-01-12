package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.JoinResponseDto
import com.letspl.oceankeeper.data.dto.SignUpBody
import com.letspl.oceankeeper.data.dto.UploadProfileImageDto
import com.letspl.oceankeeper.data.network.ApiService
import com.letspl.oceankeeper.di.module.ApiModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class JoinRepositoryImpl @Inject constructor(@ApiModule.OceanRetrofit private val apiService: ApiService): JoinRepository {
    override suspend fun signUpUser(
        deviceToken: String,
        provider: String,
        providerId: String,
        nickname: String,
        email: String,
        profile: String
    ): Response<JoinResponseDto> {
        return apiService.signUpUser(SignUpBody(deviceToken, email, nickname, profile, provider, providerId))
    }

    override suspend fun uploadProfileImage(file: File): Response<UploadProfileImageDto> {
        val requestBody: RequestBody =
            file.asRequestBody("image/*".toMediaTypeOrNull())
        val imageBody = MultipartBody.Part.createFormData("profile", "profile.jpg", requestBody)

        return withContext(Dispatchers.IO) {
            apiService.uploadProfileImageFile(imageBody)
        }
    }
}