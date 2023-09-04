package com.letspl.oceankepper.data.model

import com.letspl.oceankepper.data.dto.MyActivityItem

object MainModel {
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
}