package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.LogoutDto
import com.letspl.oceankepper.data.dto.MessageDto
import com.letspl.oceankepper.data.dto.NotificationDto
import com.letspl.oceankepper.data.dto.PostSendMessageResultDto
import com.letspl.oceankepper.data.dto.PrivacyDto
import com.letspl.oceankepper.data.model.MessageModel
import retrofit2.Response

interface PrivacyRepository {

    // PrivacyPolicy 조회
    suspend fun getPrivacyPolicy(): Response<PrivacyDto>

}