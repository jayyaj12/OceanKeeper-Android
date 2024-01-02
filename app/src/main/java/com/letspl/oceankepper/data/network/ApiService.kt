package com.letspl.oceankepper.data.network

import android.app.Notification
import com.letspl.oceankepper.data.dto.*
import com.letspl.oceankepper.data.model.ManageApplyMemberModel
import com.letspl.oceankepper.data.model.MessageModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
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

    // 메세지 상태를 읽음으로 변경
    @POST("message/detail")
    suspend fun postMessageRead(
        @Header("Authorization") token: String,
        @Body data: PostMessageDetailBodyDto
    ): Response<MessageDetailDto>

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

    // 나의 오션키퍼 활동정보 조회
    @GET("/activity/activity-info/user/{userId}")
    suspend fun getActivityInfo(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
    ): Response<GetActivityInfoDto>

    // 활동 정보 불러오기
    @GET("/activity/user/{userId}")
    suspend fun getUserActivity(
        @Header("Authorization") token: String,
        @Path("userId") userPathId: String,
        @Query("activity-id") activityId: String?,
        @Query("role") role: String,
        @Query("size") size: Int,
    ): Response<GetUserActivityDto>

    // 활동 지원 취소
    @DELETE("/activity/recruitment/application/cancel")
    suspend fun deleteApplyCancel(
        @Header("Authorization") token: String,
        @Query("application-id") applicationId: String
    ): Response<DeleteApplyDto>

    // 활동 모집 취소
    @DELETE("/activity/recruitment")
    suspend fun deleteRecruitmentCancel(
        @Header("Authorization") token: String,
        @Query("activity-id") activityId: String
    ): Response<DeleteRecruitmentDto>

    // 특정 활동 지원서 보기
    @GET("/activity/detail/application")
    suspend fun getDetailApplication(
        @Header("Authorization") token: String,
        @Query("application-id") applicationId: String
    ): Response<GetApplicationDetailDto>

    // 활동 지원서 수정
    @PATCH("/activity/recruitment/application/{applicationId}")
    suspend fun patchApplication(
        @Header("Authorization") token: String,
        @Path("applicationId") applicationId: String,
        @Body patchApplicationBody: PatchApplyApplicationBody
    ): Response<PatchApplicationDto>

    // 닉네임 중복 체크
    @GET("/auth")
    suspend fun getDuplicateNickname(
        @Query("nickname") nickname: String
    ): Response<GetCheckDuplicateNicknameResponse>

    // 닉네임 변경
    @PUT("/auth/nickname")
    suspend fun putNickname(
        @Header("Authorization") token: String,
        @Body nicknameBody: PutNicknameBody
    ): Response<PutNicknameResponse>

    // 활동 수정
    @PATCH("/activity/recruitment/{activityId}")
    suspend fun patchActivity(
        @Header("Authorization") token: String,
        @Path("activityId") activityId: String,
        @Body activityRegisterDto: EditActivityRegisterDto
    ): Response<DeleteApplyDto>

    // 신청자 리스트 불러오기
    @GET("/activity/recruitment/host/crew-info")
    suspend fun getCrewInfoList(
        @Header("Authorization") token: String,
        @Query("activity-id") activityId: String,
    ): Response<ManageApplyMemberModel.GetCrewInfoListResponseDto>

    // 신청자 리스트 불러오기
    @GET("/activity/detail/application/full")
    suspend fun getCrewDetail(
        @Header("Authorization") token: String,
        @Query("application-id") applicationId: String,
    ): Response<ManageApplyMemberModel.GetCrewDetailResponseDto>

    // 로그아웃
    @POST("/auth/logout")
    suspend fun postLogout(
        @Header("Authorization") token: String,
        @Body logoutReqDto: LogoutBody,
    ): Response<LogoutDto>

    // 탈퇴하기
    @POST("/auth/withdrawal")
    suspend fun postWithdraw(
        @Header("Authorization") token: String,
        @Body logoutReqDto: LogoutBody,
    ): Response<LogoutDto>

    // 알람 수신 여부 설정
    @FormUrlEncoded
    @POST("/notification/user/{userId}/alarm")
    suspend fun postNotificationAlarm(
        @Header("Authorization") token: String,
        @Field("alarm") alarm: Boolean,
        @Path("userId") userId: String
    ): Response<NotificationDto>

    // 알람 수신 여부 가져오기
    @GET("/notification/user/{userId}/alarm")
    suspend fun getNotificationAlarm(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<NotificationDto>

    // 알람 리스트 가져오기
    @GET("/notification/user/{userId}")
    suspend fun getNotificationList(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Query("size") size: Int,
        @Query("id") id: Int?,
    ): Response<NotificationListDto>

    // 이용약관 가져오기
    @GET("/terms")
    suspend fun getPrivacyPolicy(
        @Header("Authorization") token: String
    ): Response<PrivacyDto>

    // 크루원 상태 변경
    @POST("/activity/recruitment/host/crew-status")
    suspend fun postCrewStatus(
        @Header("Authorization") token: String,
        @Body request: ManageApplyMemberModel.PostCrewStatusBody,
    ): Response<ManageApplyMemberModel.PostCrewStatusDto>

    // 크루원 상태 변경
    @GET("/activity/recruitment/host/crew-info-file")
    @Streaming
    suspend fun getCrewInfoFile(
        @Header("Authorization") token: String,
        @Query("activity-id") activityId: String
    ): Response<ResponseBody>
}
