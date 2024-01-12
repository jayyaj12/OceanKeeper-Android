package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.GetGuideDto
import retrofit2.Response

interface GuideRepository {

    // 공지사항 조회
    suspend fun getGuide(token: String, guideId: Int?, size: Int): Response<GetGuideDto>
}