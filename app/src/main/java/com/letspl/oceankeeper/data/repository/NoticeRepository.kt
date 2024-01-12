package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.GetNoticeDto
import retrofit2.Response

interface NoticeRepository {

    // 공지사항 조회
    suspend fun getNotice(token: String, noticeId: Int?, size: Int): Response<GetNoticeDto>
}