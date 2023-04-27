package com.letspl.oceankepper.data.network

import com.letspl.oceankepper.data.dto.LoginDto
import com.letspl.oceankepper.data.dto.NaverLoginDto
import retrofit2.Response
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST
    @FormUrlEncoded
    suspend fun postAppLogin(
        provider: String,
        providerId: String
    ): Response<LoginDto>

    @GET("/v1/nid/me")
    suspend fun getNaverUserInfo(
        @Header("Authorization") token: String
    ): Response<NaverLoginDto>


}