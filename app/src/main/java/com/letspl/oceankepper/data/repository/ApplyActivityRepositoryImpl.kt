package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.ApplyApplicationDto
import com.letspl.oceankepper.data.dto.GetApplicationDetailDto
import com.letspl.oceankepper.data.dto.GetApplicationDetailResponse
import com.letspl.oceankepper.data.dto.JoinDto
import com.letspl.oceankepper.data.dto.PatchApplicationDto
import com.letspl.oceankepper.data.dto.PostApplyApplicationBody
import com.letspl.oceankepper.data.dto.SignUpBody
import com.letspl.oceankepper.data.dto.UploadProfileImageDto
import com.letspl.oceankepper.data.model.UserModel
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

    override suspend fun getDetailApplication(applicationId: String): Response<GetApplicationDetailDto> {
        return apiService.getDetailApplication("Bearer ${UserModel.userInfo.token.accessToken}", applicationId)
    }

    override suspend fun patchApplication(
        applicationId: String,
        patchApplicationBody: GetApplicationDetailResponse
    ): Response<PatchApplicationDto> {
        return apiService.patchApplication("Bearer ${UserModel.userInfo.token.accessToken}", applicationId, patchApplicationBody)
    }
}