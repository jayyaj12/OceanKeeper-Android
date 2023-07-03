package com.letspl.oceankepper.data.network

import com.letspl.oceankepper.data.dto.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    // 네이버 인증키로 정보 불러오기
    @GET("/v1/nid/me")
    suspend fun getNaverUserInfo(
        @Header("Authorization") token: String
    ): Response<NaverLoginDto>

    // 프로필 이미지 업로드
    @Multipart
    @POST("image/profile")
    suspend fun uploadImageFile(
        @Part profile: MultipartBody.Part
    ): Response<UploadProfileImageDto>

    // 로그인
    @POST("auth/login")
    suspend fun loginUser(
        @Body loginBody: LoginBody
    ): Response<LoginUserResponseDto>

    // 회원가입
    @POST("auth/signup")
    suspend fun signUpUser(
       @Body signUpBody: SignUpBody
    ): Response<JoinDto>
}
