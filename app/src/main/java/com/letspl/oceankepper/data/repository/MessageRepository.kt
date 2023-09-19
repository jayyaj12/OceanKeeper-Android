package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.dto.MessageDto
import retrofit2.Response

interface MessageRepository {
    // 다가오는 일정 조회
    suspend fun getMessage(token: String, messageId: Long?, size: Int, type: String?, user:String): Response<MessageDto>

}