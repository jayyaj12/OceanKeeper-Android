package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.GetNoticeDto
import com.letspl.oceankeeper.data.network.ApiService
import com.letspl.oceankeeper.di.module.ApiModule
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