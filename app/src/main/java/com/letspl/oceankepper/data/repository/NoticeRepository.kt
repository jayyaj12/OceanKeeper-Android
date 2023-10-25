package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.GetNoticeDto
import com.letspl.oceankepper.data.dto.LoginDto
import com.letspl.oceankepper.data.dto.LoginResponseDto
import com.letspl.oceankepper.data.dto.LoginUserResponseDto
import com.letspl.oceankepper.data.dto.NaverLoginDto
import retrofit2.Response

interface NoticeRepository {

    // 공지사항 조회
    suspend fun getNotice(token: String, noticeId: Int?, size: Int): Response<GetNoticeDto>
}