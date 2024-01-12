package com.letspl.oceankeeper.data.model

object NotificationModel {
    // 알림 조회 마지막 인지 여부
    var lastMemo: Boolean = false
    // 알림 조회 마지막 activityId
    var lastMemoId: Int? = null
}