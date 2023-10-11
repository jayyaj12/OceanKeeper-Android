package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.*
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.di.module.ApiModule
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

class ActivityRepositoryImpl @Inject constructor(@ApiModule.OceanRetrofit private val apiService: ApiService) :
    ActivityRepository {


    // 활동 등록하기
    override suspend fun activityRegister(
        activityStory: String,
        etc: String,
        garbageCategory: String,
        keeperImageUrl: String?,
        keeperIntroduction: String,
        location: ActivityRegisterLocationDto,
        locationTag: String,
        preparation: String,
        programDetails: String,
        quota: Int,
        recruitEndAt: String,
        recruitStartAt: String,
        rewards: String,
        startAt: String,
        storyImageUrl: String?,
        thumbnailUrl: String?,
        title: String,
        transportation: String,
        userId: String
    ): Response<ActivityRegisterResultDto> {
        return apiService.activityRegister(
            "Bearer ${UserModel.userInfo.token.accessToken}",
            ActivityRegisterDto(
                activityStory,
                etc,
                garbageCategory,
                keeperImageUrl,
                keeperIntroduction,
                location,
                locationTag,
                preparation,
                programDetails,
                quota,
                recruitEndAt,
                recruitStartAt,
                rewards,
                startAt,
                storyImageUrl,
                thumbnailUrl,
                title,
                transportation,
                userId
            )
        )
    }

    // 키퍼 소개 이미지 업로드
    override suspend fun uploadKeeperImage(file: File?): Response<UploadProfileImageDto> {
        var imageBody: MultipartBody.Part? = null
        if(file != null) {
            val requestBody: RequestBody =
                file.asRequestBody("image/*".toMediaTypeOrNull())
            imageBody = MultipartBody.Part.createFormData("keeper", "keeper.jpg", requestBody)
        }

        return withContext(Dispatchers.IO) {
            apiService.uploadKeeperImageFile(imageBody)
        }
    }

    // 썸네일 이미지 업로드
    override suspend fun uploadThumbnailImage(file: File?): Response<UploadProfileImageDto> {
        var imageBody: MultipartBody.Part? = null
        if(file != null) {
            val requestBody: RequestBody =
                file.asRequestBody("image/*".toMediaTypeOrNull())
            imageBody = MultipartBody.Part.createFormData("thumbnail", "thumbnail.jpg", requestBody)
        }

        return withContext(Dispatchers.IO) {
            apiService.uploadThumbnailImageFile(imageBody)
        }
    }

    // 활동 스토리 업로드
    override suspend fun uploadStoryImage(file: File?): Response<UploadProfileImageDto> {
        var imageBody: MultipartBody.Part? = null
        if(file != null) {
            val requestBody: RequestBody =
                file.asRequestBody("image/*".toMediaTypeOrNull())
            imageBody = MultipartBody.Part.createFormData("story", "story.jpg", requestBody)

        }
        return withContext(Dispatchers.IO) {
            apiService.uploadStoryImageFile(imageBody)
        }
    }

    // 활동 프로젝트명 불러오기
    override suspend fun getActivityProject(): Response<GetActivityRecruitmentActivityNameResultDto> {
        return apiService.getProjectList("Bearer ${UserModel.userInfo.token.accessToken}")
    }

    // 크루원 불러오기
    override suspend fun getCrewNickname(activityId: String): Response<GetActivityRecruitmentCrewNameResultDto> {
        return apiService.getCrewNickName("Bearer ${UserModel.userInfo.token.accessToken}", activityId)
    }
}