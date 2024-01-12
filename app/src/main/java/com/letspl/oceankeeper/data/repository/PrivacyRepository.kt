package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.PrivacyDto
import retrofit2.Response

interface PrivacyRepository {

    // PrivacyPolicy 조회
    suspend fun getPrivacyPolicy(): Response<PrivacyDto>

}