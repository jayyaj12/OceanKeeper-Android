package com.letspl.oceankeeper.data.model

import com.letspl.oceankeeper.data.dto.GetMyActivityDetailItem
import com.letspl.oceankeeper.data.dto.MyActivityItem

object MainModel {
    var fixLocation: IntArray? = null
    var screenHeightSize = 0
    var fixViewYPosition: Float? = null
    var activityList = arrayListOf<MyActivityItem>()
    // 활동 조회 마지막 인지 여부
    var lastActivity: Boolean = false
    // 활동 조회 마지막 activityId
    var lastActivityId: String? = null
    // 지역 임시 선택값
    var tempAreaPosition = -1
    // 쓰레기 임시 선택값
    var tempGarbageCategoryPosition = -1
    // 지역 선택값
    var areaPosition = -1
    // 지역 선택값
    var garbageCategoryPosition = -1
    // 선택된 activityId
    var clickedActivityId = ""
    var hostNickName = "" // 상세 화면 호스트 닉네임
    // 문의하기 클릭 시 닉네임 표시에 사용
    lateinit var clickedActivityItem: GetMyActivityDetailItem
}