package com.letspl.oceankepper.data.model

import com.letspl.oceankepper.data.dto.MessageItemDto

object MessageModel {
    var isLast = false // 쪽지 마지막까지 불러온건지 확인
    var messageId: Long? = null
    var messageList = mutableListOf<MessageItemDto>()
}