package com.letspl.oceankepper.data.model

import com.letspl.oceankepper.data.dto.GetActivityRecruitmentActivityNameList
import com.letspl.oceankepper.data.dto.GetActivityRecruitmentCrewNameList
import com.letspl.oceankepper.data.dto.MessageItemDto
import kotlinx.serialization.Serializable

object MessageModel {
    var isLast = false // 쪽지 마지막까지 불러온건지 확인
    var messageId: Long? = null
    var messageList = mutableListOf<MessageItemDto>()
    var clickedMessageItem: MessageItemDto? = null
    var typeTabItem = "ALL" // 타입 탭 선택 아이템
    var typeSpinnerClickPos = 0 // 쪽지 유형 선택된 아이템 인덱스
    var activityNameSpinnerClickPos = 0 // 활동 프로젝트 선택된 아이템 인덱스
    var projectNameList = listOf<GetActivityRecruitmentActivityNameList>() // 활동 프로젝트 명 list
    var crewNicknameList = arrayListOf<String>() // 크루원 닉네임 리스트

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

    @Serializable
    data class SendMessageRequestBody(
        val activityId: String,
        val contents: String,
        val targetNicknames: List<String>,
        val type: String
    )
}