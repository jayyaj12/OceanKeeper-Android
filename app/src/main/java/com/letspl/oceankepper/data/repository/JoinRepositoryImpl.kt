package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.UploadProfileImageDto
import com.letspl.oceankepper.data.network.ApiService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class JoinRepositoryImpl @Inject constructor(private val apiService: ApiService): JoinRepository {
    override suspend fun joinAccount(
        deviceToken: String,
        provider: String,
        providerId: String,
        nickname: String,
        email: String,
        profile: String
    ) {

    }

    override suspend fun uploadProfileImage(file: File): Response<UploadProfileImageDto> {
        val requestBody: RequestBody =
            file.asRequestBody("image/*".toMediaTypeOrNull())
        val imageBody = MultipartBody.Part.createFormData("pictureFile", "profile.jpg", requestBody)

        return withContext(Dispatchers.IO) {
            apiService.uploadImageFile(imageBody)
        }
    }
}