package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.*
import retrofit2.Response
import java.io.File

interface ActivityRepository {

    // 활동 등록
    suspend fun activityRegister(activityStory: String, etc: String, garbageCategory: String, keeperImageUrl:String?, keeperIntroduction:String, location: ActivityRegisterLocationDto, locationTag:String, preparation:String, programDetails:String, quota:Int, recruitEndAt:String, recruitStartAt:String, rewards:String, startAt:String, storyImageUrl:String?, thumbnailUrl:String?, title:String, transportation:String, userId:String): Response<ActivityRegisterResultDto>
    // 키퍼 소개 이미지 업로드
    suspend fun uploadKeeperImage(file: File?): Response<UploadProfileImageDto>
    // 썸네일 이미지 업로드
    suspend fun uploadThumbnailImage(file: File?): Response<UploadProfileImageDto>
    // 활동 story 이미지 업로드
    suspend fun uploadStoryImage(file: File?): Response<UploadProfileImageDto>
}