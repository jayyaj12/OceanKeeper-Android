package com.letspl.oceankepper.data.network

import com.letspl.oceankepper.data.dto.*
import com.letspl.oceankepper.data.model.MessageModel
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

    // 프로필 이미지 수정
    @Multipart
    @POST("image/edit/profile")
    suspend fun uploadEditProfileImageFile(
        @Header("Authorization") token: String,
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
    ): Response<JoinResponseDto>

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

    // 메세지함 확인
    /*
        id: 메세지 아이디
        size: 메세지 개수
        type: 메세지 타입(REJECT, NOTICE, PRIVATE, ALL) 중 하나 NULL 로 보낼 경우 ALL 로 default
        user: 메세지 보내는 유저 아이디
     */
    @GET("message/inbox")
    suspend fun getMessage(
        @Header("Authorization") token: String,
        @Query("id") id: Long?,
        @Query("size") size:Int,
        @Query("type") type: String?,
        @Query("user") user: String
    ): Response<MessageDto>

    // 활동 프로젝트 리스트 불러오기
    @GET("activity/recruitment/host/activity-name")
    suspend fun getProjectList(
        @Header("Authorization") token: String
    ): Response<GetActivityRecruitmentActivityNameResultDto>

    // 활동의 크루원 닉네임 불러오기
    @GET("activity/recruitment/host/crew-nickname")
    suspend fun getCrewNickName(
        @Header("Authorization") token: String,
        @Query("activity-id") activityId: String
    ): Response<GetActivityRecruitmentCrewNameResultDto>

    // 쪽지 보내기
    @POST("/message")
    suspend fun postMessage(
        @Header("Authorization") token: String,
        @Body message: MessageModel.SendMessageRequestBody
    ): Response<PostSendMessageResultDto>

    @GET("/notice")
    suspend fun getNotice(
        @Header("Authorization") token: String,
        @Query("notice-id") noticeId: Int?,
        @Query("size") size: Int
    ): Response<GetNoticeDto>

    @GET("/guide")
    suspend fun getGuide(
        @Header("Authorization") token: String,
        @Query("notice-id") noticeId: Int?,
        @Query("size") size: Int
    ): Response<GetGuideDto>

    // 활동정보 불러오기
    @GET("/activity/activity-info/user/{userId}")
    suspend fun getActivityInfo(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
    ): Response<GetActivityInfoDto>

}
