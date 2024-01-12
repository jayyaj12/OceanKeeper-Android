package com.letspl.oceankeeper.data.model

import com.letspl.oceankeeper.data.dto.MessageItemDto
import com.letspl.oceankeeper.util.MessageEnterType
import kotlinx.serialization.Serializable

object MessageModel {
    var isLast = false // 쪽지 마지막까지 불러온건지 확인
    var messageId: Long? = null
    var messageList = mutableListOf<MessageItemDto>()
    var clickedMessageItem: MessageItemDto? = null
    var typeTabItem = "ALL" // 타입 탭 선택 아이템
    var typeSpinnerClickPos = 0 // 쪽지 유형 선택된 아이템 인덱스
    var activityNameSpinnerClickPos = 0 // 활동 프로젝트 선택된 아이템 인덱스
    var projectNameList = listOf<MessageSpinnerProjectNameItem>() // 활동 프로젝트 명 list
    var crewNicknameList = arrayListOf<MessageSpinnerCrewNicknameItem>() // 크루원 닉네임 리스트
    var receiveList = arrayListOf<String>() // 받는 사람 list
    var enterType = MessageEnterType.ActivityMessage // 진입 여부에 따라 Spinner Block 처리가 달라짐 그에 대한 변수
    var isClickedMessageType = false // 쪽지 보내기 메세지 타입 선택 여부
    var isClickedProjectName = false // 쪽지 보내기 프로젝트 명 선택 여부 
    var isClickedReceive = false // 쪽지 보내기 받는사람 선택 여부 

    // 쪽지유형 스피너 내부 아이템 data class
    data class MessageSpinnerMessageTypeItem(
        val itemName: String, // 아이템 명
        var isChecked: Boolean // 선택됨 여부
    )

    data class MessageSpinnerProjectNameItem(
        val activityId: String,
        val title: String, // 활동명
        var isChecked: Boolean // 선택됨 여부
    )

    data class MessageSpinnerCrewNicknameItem(
        val nickname: String, // 닉네임
        var isChecked: Boolean // 선택됨 여부
    )

    @Serializable
    data class SendMessageRequestBody(
        val activityId: String,
        val contents: String,
        val targetNicknames: List<String>,
        val type: String
    )
}