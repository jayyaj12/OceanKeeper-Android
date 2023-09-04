package com.letspl.oceankepper.data.network

import com.letspl.oceankepper.data.dto.*
import com.letspl.oceankepper.data.model.UserModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // 네이버 인증키로 정보 불러오기
    @GET("/v1/nid/me")
    suspend fun getNaverUserInfo(
        @Header("Authorization") token: String
    ): Response<NaverLoginDto>

    // 프로필 이미지 업로드
    @Multipart
    @POST("image/profile")
    suspend fun uploadProfileImageFile(
        @Part profile: MultipartBody.Part
    ): Response<UploadProfileImageDto>

    //  썸네일 이미지 업로드
    @Multipart
    @POST("image/thumbnail")
    suspend fun uploadThumbnailImageFile(
        @Part profile: MultipartBody.Part?
    ): Response<UploadProfileImageDto>

    // 활동 스토리 이미지 업로드
    @Multipart
    @POST("image/story")
    suspend fun uploadStoryImageFile(
        @Part profile: MultipartBody.Part?
    ): Response<UploadProfileImageDto>

    // 키퍼 소개 이미지 업로드
    @Multipart
    @POST("image/keeper")
    suspend fun uploadKeeperImageFile(
        @Part profile: MultipartBody.Part?
    ): Response<UploadProfileImageDto>

    // 로그인
    @POST("auth/login")
    suspend fun loginUser(
        @Body loginBody: LoginBody
    ): Response<LoginResponseDto>

    // 회원가입
    @POST("auth/signup")
    suspend fun signUpUser(
        @Body signUpBody: SignUpBody
    ): Response<JoinDto>

    // 활동 등록
    @POST("activity/recruitment")
    suspend fun activityRegister(
        @Header("Authorization") token: String, @Body activityRegisterDto: ActivityRegisterDto
    ): Response<ActivityRegisterResultDto>

    // 다가오는 일정 조회
    @GET("activity/schedule/user/{userId}")
    suspend fun getComingSchedule(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<GetComingScheduleResponse>

    // 활동 조회
    @GET("activity")
    suspend fun getMyActivity(
        @Header("Authorization") token: String,
        @Query("activity-id") activityId: String?,
        @Query("garbage-category") garbageCategory: String?,
        @Query("location-tag") locationTag: String?,
        @Query("size") size: Int,
        @Query("status") status: String?,
    ): Response<GetMyActivityResponse>

    // 특정 활동 상세 보기
    @GET("activity/detail")
    suspend fun getMyActivityDetail(
        @Header("Authorization") token: String,
        @Query("activity-id") activityId: String?,
    ): Response<GetMyActivityDetailResponse>

    // 활동 지원
    @POST("activity/recruitment/application")
    suspend fun postRecruitmentApplication(
        @Header("Authorization") token: String,
        @Body activityBody: PostApplyApplicationBody,
    ): Response<ApplyApplicationDto>
}
