package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.MessageDetailDto
import com.letspl.oceankeeper.data.dto.MessageDto
import com.letspl.oceankeeper.data.dto.PostMessageDetailBodyDto
import com.letspl.oceankeeper.data.dto.PostSendMessageResultDto
import com.letspl.oceankeeper.data.model.MessageModel
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.network.ApiService
import com.letspl.oceankeeper.di.module.ApiModule
import retrofit2.Response
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(@ApiModule.OceanService private val service: ApiService): MessageRepository {
    override suspend fun getMessage(
        token: String,
        messageId: Long?,
        size: Int,
        type: String?,
        user: String
    ): Response<MessageDto> {
        return service.getMessage(token, messageId, size, type, user)
    }

    override suspend fun postMessage(
        messageBody: MessageModel.SendMessageRequestBody
    ): Response<PostSendMessageResultDto> {
        return service.postMessage("Bearer ${UserModel.userInfo.token.accessToken}", messageBody)
    }

    override suspend fun postMessageRead(dataBody: PostMessageDetailBodyDto): Response<MessageDetailDto> {
        return service.postMessageRead("Bearer ${UserModel.userInfo.token.accessToken}", dataBody)
    }

    override suspend fun deleteMessage(messageId: Int): Response<MessageDetailDto> {
        return service.deleteMessage("Bearer ${UserModel.userInfo.token.accessToken}", messageId)
    }
}