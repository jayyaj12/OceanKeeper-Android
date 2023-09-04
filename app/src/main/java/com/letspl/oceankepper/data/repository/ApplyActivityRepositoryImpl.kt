package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.ApplyApplicationDto
import com.letspl.oceankepper.data.dto.JoinDto
import com.letspl.oceankepper.data.dto.PostApplyApplicationBody
import com.letspl.oceankepper.data.dto.SignUpBody
import com.letspl.oceankepper.data.dto.UploadProfileImageDto
import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.di.module.ApiModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class ApplyActivityRepositoryImpl @Inject constructor(@ApiModule.OceanRetrofit private val apiService: ApiService): ApplyActivityRepository {

    override suspend fun postRecruitmentApplication(
        token: String,
        activity: PostApplyApplicationBody
    ): Response<ApplyApplicationDto> {
       return apiService.postRecruitmentApplication(token, activity)
    }
}