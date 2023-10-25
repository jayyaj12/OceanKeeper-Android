package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.GetNoticeDto
import com.letspl.oceankepper.data.dto.LoginBody
import com.letspl.oceankepper.data.dto.LoginResponseDto
import com.letspl.oceankepper.data.dto.LoginUserResponseDto
import com.letspl.oceankepper.data.dto.NaverLoginDto
import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.di.module.ApiModule
import retrofit2.Response
import javax.inject.Inject

class NoticeRepositoryImpl @Inject constructor(@ApiModule.OceanRetrofit private val oceanApiService: ApiService) :
    NoticeRepository {

    override suspend fun getNotice(
        token: String,
        noticeId: Int?,
        size: Int
    ): Response<GetNoticeDto> {
        return oceanApiService.getNotice(token, noticeId, size)
    }
}