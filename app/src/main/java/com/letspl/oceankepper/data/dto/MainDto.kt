package com.letspl.oceankepper.data.dto

@kotlinx.serialization.Serializable
data class GetComingScheduleResponse(
    val response: GetComingScheduleItem
)

@kotlinx.serialization.Serializable
data class GetComingScheduleItem(
    val activities: List<ComingScheduleItem>
)

@kotlinx.serialization.Serializable
data class GetMyActivityResponse(
    val response: GetMyActivityItem
)

@kotlinx.serialization.Serializable
data class GetMyActivityItem(
    val activities: List<MyActivityItem>,
    val meta: GetMyActivityMeta
)

@kotlinx.serialization.Serializable
data class GetMyActivityMeta(
    val last: Boolean,
    val size: Int
)
