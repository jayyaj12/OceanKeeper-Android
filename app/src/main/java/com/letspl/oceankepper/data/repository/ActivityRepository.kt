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
    // 활동 프로젝트명 불러오기
    suspend fun getActivityProject(): Response<GetActivityRecruitmentActivityNameResultDto>
    // 크루원 닉네임 불러오기
    suspend fun getCrewNickname(activityId: String): Response<GetActivityRecruitmentCrewNameResultDto>
    // 활동정보 불러오기
    suspend fun getActivityInfo(userId: String): Response<GetActivityInfoDto>
    // 프로필 수정
    suspend fun uploadEditProfileImage(imageFile: File): Response<UploadProfileImageDto>
    // 내활동보기
    suspend fun getUserActivity(activityId: String?, size:Int, status: String, userId: String): Response<GetUserActivityDto>
}