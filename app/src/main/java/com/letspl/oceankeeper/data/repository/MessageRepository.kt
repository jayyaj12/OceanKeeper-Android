package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.MessageDetailDto
import com.letspl.oceankeeper.data.dto.MessageDto
import com.letspl.oceankeeper.data.dto.PostMessageDetailBodyDto
import com.letspl.oceankeeper.data.dto.PostSendMessageResultDto
import com.letspl.oceankeeper.data.model.MessageModel
import retrofit2.Response

interface MessageRepository {
    // 쪽지 조회하기
    suspend fun getMessage(token: String, messageId: Long?, size: Int, type: String?, user:String): Response<MessageDto>

    // 쪽지 보내기
    suspend fun postMessage(messageBody: MessageModel.SendMessageRequestBody): Response<PostSendMessageResultDto>

    // 쪽지의 상태를 읽음으로 변경
    suspend fun postMessageRead(dataBody: PostMessageDetailBodyDto): Response<MessageDetailDto>

    // 쪽지 삭제
    suspend fun deleteMessage(messageId: Int): Response<MessageDetailDto>

}