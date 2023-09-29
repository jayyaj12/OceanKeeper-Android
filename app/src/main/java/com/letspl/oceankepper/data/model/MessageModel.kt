package com.letspl.oceankepper.data.model

import com.letspl.oceankepper.data.dto.MessageItemDto

object MessageModel {
    var isLast = false // 쪽지 마지막까지 불러온건지 확인
    var messageId: Long? = null
    var messageList = mutableListOf<MessageItemDto>()
    var clickedMessageItem: MessageItemDto? = null
    var typeTabItem = "ALL" // 타입 탭 선택 아이템
    var typeSpinnerClickPos = 0 // 쪽지 유형 선택된 아이템 인덱스

    // 스피너 내부 아이템 data class
    data class MessageSpinnerItem(
        val itemName: String, // 아이템 명
        var isChecked: Boolean // 선택됨 여부
    )
}